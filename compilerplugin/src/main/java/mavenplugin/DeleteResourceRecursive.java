package mavenplugin;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteResourceRecursive extends SimpleFileVisitor<Path> {
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException $) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes $) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }
}
