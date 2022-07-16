package br.eti.arthurgregorio.sistemarquivos;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path) throws IOException {

        if(!path.getFileName().toString().contains(".")) {
            throw new UnsupportedEncodingException("This command should be used with files only");
        }

        if (path.endsWith(".txt")) {
            Files.readAllLines(path)
                    .forEach(System.out::println);
        } else {
            throw new UnsupportedEncodingException("Extension not supported");
        }

    }
}
