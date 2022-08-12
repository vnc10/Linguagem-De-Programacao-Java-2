package br.edu.utfpr;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class SalesReader {

    private final List<Sale> sales;

    public SalesReader(String salesFile) {

        final var dataStream = ClassLoader.getSystemResourceAsStream(salesFile);

        if (dataStream == null) {
            throw new IllegalStateException("File not found or is empty");
        }

        final var builder = new CsvToBeanBuilder<Sale>(new InputStreamReader(dataStream, StandardCharsets.UTF_8));

        sales = builder
                .withType(Sale.class)
                .withSeparator(';')
                .build()
                .parse();
    }

    public BigDecimal totalOfCompletedSales() {
        return sales.stream()
                .filter(Sale::isCompleted)
                .map(Sale::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalOfCancelledSales() {
        return sales.stream()
                .filter(Sale::isCancelled)
                .map(Sale::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Sale> mostRecentCompletedSale() {

        return sales.stream()
                .max(Comparator.comparing(Sale::getSaleDate));
    }

    public long daysBetweenFirstAndLastCancelledSale() {
        return ChronoUnit.DAYS.between(
                sales.stream()
                        .filter(Sale::isCancelled)
                        .min(Comparator.comparing(Sale::getSaleDate))
                        .get()
                        .getSaleDate(),
                sales.stream()
                        .filter(Sale::isCancelled)
                        .max(Comparator.comparing(Sale::getSaleDate))
                        .get()
                        .getSaleDate());
    }

    public BigDecimal totalCompletedSalesBySeller(String sellerName) {
        return sales.stream()
                .filter(Sale::isCompleted)
                .filter(sale -> sale.getSeller().equals(sellerName))
                .map(Sale::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long countAllSalesByManager(String managerName) {
        return sales.stream()
                .filter(sale -> sale.getManager().equals(managerName))
                .count();
    }

    public BigDecimal totalSalesByStatusAndMonth(Sale.Status status, Month... months) {
        List<Month> monthList = Arrays.stream(months).toList();

        BigDecimal sum = new BigDecimal(0);
        for (Sale sale : sales) {
            if (sale.getStatus().equals(status)) {
                for (Month month : monthList) {
                    if (sale.getSaleDate().getMonth().equals(month)) {
                        sum = sum.add(sale.getValue());
                    }
                }
            }
        }
        return sum;
    }

    public Map<String, Long> countCompletedSalesByDepartment() {

        return sales.stream()
                .filter(Sale::isCompleted)
                .collect(Collectors.groupingBy(Sale::getDepartment, Collectors.counting()));
    }

    public Map<Integer, Map<String, Long>> countCompletedSalesByPaymentMethodAndGroupingByYear() {
        return sales.stream()
                .filter(Sale::isCompleted)
                .collect(Collectors.groupingBy(sale -> sale.getSaleDate().getYear(), (Collectors.groupingBy(Sale::getPaymentMethod, Collectors.counting()))));
    }

    public Map<String, BigDecimal> top3BestSellers() {
        return sales.stream()
                .filter(Sale::isCompleted)
                .collect(Collectors.groupingBy(Sale::getSeller, mapping(Sale::getValue, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .entrySet().stream()
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
