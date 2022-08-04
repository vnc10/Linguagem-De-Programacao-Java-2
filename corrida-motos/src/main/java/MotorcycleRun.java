import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MotorcycleRun {

    // array de dados, nele vamos gravar de maneira sincronizada os dados
    private final List<String> dados;

    public MotorcycleRun() throws Exception {

        this.dados = new ArrayList<>();

        final var threads = new ArrayList<Thread>();


        // cria 10 threads e da um trabalho a elas
        for (int i = 1; i < 11; i++) {
            final int current = i;
            threads.add(new Thread(() -> gravar("Compedidor %" + current)));
        }

        // da start em todas as threas
        try {
            threads.forEach(Thread::start);
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 21));
            for (int i = 0; i < 10; i++) {
                threads.get(i).join();
            }
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<String> getDados() {
        return dados;
    }

    // metodo que grava, sincronizando o acesso na collection para evitar que dados sejam perdidos devido a concorrencia
    public void gravar(String name) {
        synchronized (this.dados) {
            this.dados.add(name);
        }
    }

    synchronized int calculatePositionPoints(int pos) {
        if (pos % 10 == 0) {
            return 10;
        } else if (pos % 10 == 1) {
            return 9;
        } else if (pos % 10 == 2) {
            return 8;
        } else if (pos % 10 == 3) {
            return 7;
        } else if (pos % 10 == 4) {
            return 6;
        } else if (pos % 10 == 5) {
            return 5;
        } else if (pos % 10 == 6) {
            return 4;
        } else if (pos % 10 == 7) {
            return 3;
        } else if (pos % 10 == 8) {
            return 2;
        } else if (pos % 10 == 9) {
            return 1;
        }
        return pos;
    }

    public Map<String, Integer> calculatePoints(List<String> dados) {
        Map<String, Integer> competitorWithPoints = new HashMap<>();
        for (int i = 0; i < dados.size(); i++) {
            competitorWithPoints.put(dados.get(i), calculatePositionPoints(i));
        }
        return competitorWithPoints;
    }


    public static void main(String[] args) throws Exception {
        MotorcycleRun run1 = new MotorcycleRun();
        Map<String, Integer> run1points = run1.calculatePoints(run1.getDados());
        MotorcycleRun run2 = new MotorcycleRun();
        Map<String, Integer> run2points = run2.calculatePoints(run2.getDados());
        MotorcycleRun run3 = new MotorcycleRun();
        Map<String, Integer> run3points = run3.calculatePoints(run3.getDados());
        MotorcycleRun run4 = new MotorcycleRun();
        Map<String, Integer> run4points = run3.calculatePoints(run4.getDados());
        MotorcycleRun run5 = new MotorcycleRun();
        Map<String, Integer> run5points = run3.calculatePoints(run5.getDados());
        MotorcycleRun run6 = new MotorcycleRun();
        Map<String, Integer> run6points = run3.calculatePoints(run6.getDados());
        MotorcycleRun run7 = new MotorcycleRun();
        Map<String, Integer> run7points = run3.calculatePoints(run7.getDados());
        MotorcycleRun run8 = new MotorcycleRun();
        Map<String, Integer> run8points = run3.calculatePoints(run8.getDados());
        MotorcycleRun run9 = new MotorcycleRun();
        Map<String, Integer> run9points = run3.calculatePoints(run9.getDados());
        MotorcycleRun run10 = new MotorcycleRun();
        Map<String, Integer> run10points = run3.calculatePoints(run10.getDados());
        Map<String, Integer> competitorWithTotalPoints = new HashMap<>();
        for (int i = 1; i < 11; i++) {
            int sum = 0;
            sum = run1points.get("Compedidor %" + i) + run2points.get("Compedidor %" + i) + run3points.get("Compedidor %" + i)
                    + run4points.get("Compedidor %" + i) + run5points.get("Compedidor %" + i) + run6points.get("Compedidor %" + i)
                    + run7points.get("Compedidor %" + i) + run8points.get("Compedidor %" + i) + run9points.get("Compedidor %" + i)
                    + run10points.get("Compedidor %" + i);
            competitorWithTotalPoints.put("Competitor %" + i, sum);
        }
        sort(competitorWithTotalPoints);
    }

    static void sort(Map<String, Integer> points) {
        final Map<String, Integer> sortPoints = points.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        List<String> competitor = new ArrayList<>();
        List<Integer> finalPoints = new ArrayList<>();

        for (Map.Entry<String, Integer> comparator : sortPoints.entrySet()) {
            competitor.add(comparator.getKey());
            finalPoints.add(comparator.getValue());
        }

        System.out.println("==== Podio ====");
        for (int i = 0; i < 3; i++) {
            System.out.println(competitor.get(i) + " com " + finalPoints.get(i) + " pontos");
        }
        System.out.println("==== Tabela de pontos ====");
        for (int i = 0; i < 10; i++) {
            System.out.println(competitor.get(i) + " com " + finalPoints.get(i) + " pontos");
        }
    }
}