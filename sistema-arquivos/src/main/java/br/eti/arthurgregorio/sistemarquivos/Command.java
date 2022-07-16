package br.eti.arthurgregorio.sistemarquivos;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {

            File dir = new File(String.valueOf(path));
            File[] filesList = dir.listFiles();

            for(File f : filesList) {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                if (basicFileAttributes.isDirectory())
                    System.out.println(f.getName());
                if (basicFileAttributes.isRegularFile()) {
                    System.out.println(f.getName());
                }
            }
            return path;
        }
    },
    SHOW() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        public String getParameters() {
            return Arrays.stream(parameters).toList().get(1);
        }
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) throws IOException {
            FileReader fileReader = new FileReader();
            fileReader.read(path);
            return path.getParent();
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) throws IOException {
            Files.walk(path);
            if(!path.getParent().toString().contains("hd")) {
                throw new UnsupportedEncodingException("You are already in the root");
            }
            return path.getParent();
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        public String getParameters() {
            return Arrays.stream(parameters).toList().get(1);
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) throws IOException {

            if(path.getFileName().toString().contains(".")){
                throw new UnsupportedEncodingException("Can't open files, just directories");
            }
            Files.walk(path);
            return path;
        }
    },
    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        public String getParameters() {
            return Arrays.stream(parameters).toList().get(1);
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) throws IOException {
            BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
            System.out.println("Is directory " + basicFileAttributeView.readAttributes().isDirectory());
            System.out.println("Size " + basicFileAttributeView.readAttributes().size());
            System.out.println("Created on " + basicFileAttributeView.readAttributes().creationTime());
            System.out.println("Last acess time " + basicFileAttributeView.readAttributes().lastAccessTime());
            return path.getParent();
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Saindo...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    String getParameters() {
        return "";
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }

        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}
