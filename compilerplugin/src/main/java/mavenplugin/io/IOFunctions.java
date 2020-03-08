package mavenplugin.io;

import mavenplugin.DeleteResourceRecursive;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class IOFunctions {

    public static void touch(Path timeStampFile) {
        try {
            Files.createFile(timeStampFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void deleteFiles(Path file) {
        try {
            Files.walkFileTree(file, new DeleteResourceRecursive());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Stream<Path> walkFile(File file) {
        try {
            return Files.walk(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
