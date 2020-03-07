package mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "inc", defaultPhase = LifecyclePhase.PRE_CLEAN)
public class IncrementalMojo extends AbstractMojo {

    private static final String TIMESTAMP_FILE = "buildcheck.timestamp";
    private static final List<String> sourceComponents = Arrays.asList("java", "scala", "resources" );

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
    private List<String> compileSourceRoots;

    @Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true, required = true)
    private File outputDirectory;

    public void execute() {

        long start = System.currentTimeMillis();
        checkForModification();
        long total = System.currentTimeMillis() - start;
        info(String.format("Total time %s ms", total));
    }

    private void checkForModification() {
        LocalDateTime codeCompileAt = classCompileTime(outputDirectory);
        LocalDateTime codeChangedAt = codeChangeTime(compileSourceRoots);

        info(String.format("Code compiled at %s", codeCompileAt));
        info(String.format("Code changed at %s", codeChangedAt));

        if (codeChangedAt.isAfter(codeCompileAt)) {
            prepareForCompilation(outputDirectory);
        } else {
            nothingToClean();
        }
    }

    private void prepareForCompilation(File targetLocation) {

        Path rootTarget = targetLocation.getParentFile().toPath();
        info(String.format("Changed detected - cleaning %s", rootTarget));

        Stream.of(rootTarget)
                .filter(Files::exists)
                .forEach(this::deleteFiles);

        rootTarget.toFile().mkdir();
        Path timeStampFile = new File(rootTarget.toFile(), TIMESTAMP_FILE).toPath();
        touch(timeStampFile);

    }

    private void touch(Path timeStampFile) {
        try {
            Files.createFile(timeStampFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void deleteFiles(Path file) {
        try {
            Files.walkFileTree(file, new DeleteResourceRecusive());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void nothingToClean() {
        info("Nothing to clean - Source and target are up to date" );
        project.getProperties().setProperty("skipTests", "true" );
    }

    private LocalDateTime codeChangeTime(List<String> compileSourceRoots) {

        Stream<File> javaSourceLocation = compileSourceRoots.stream()
                .filter(f -> f.endsWith("java" ))
                .map(File::new);

        Stream<File> rootSourceLocation = javaSourceLocation.map(File::getParentFile);

        List<File> resourceToScan = rootSourceLocation
                .flatMap(this::sourceLocations)
                .filter(File::exists)
                .collect(Collectors.toList());

        return mostRecentUpdateTime(resourceToScan);
    }

    private Stream<File> sourceLocations(File parentLocation) {

        Stream<File> sourceCode = sourceCodeLocation(parentLocation);
        Stream<File> testCode = testCodeLocation(parentLocation);
        Stream<File> configCode = configFilesLocation(parentLocation);

        Stream<File> codes = Stream.concat(sourceCode, testCode);
        return Stream.concat(codes, configCode);
    }

    private Stream<File> configFilesLocation(File parentLocation) {
        String twoLevelUp = parentLocation.getParentFile().getParent();
        return Stream.of(Paths.get(twoLevelUp, "pom.xml" ))
                .map(Path::toFile);
    }

    private Stream<File> testCodeLocation(File parentLocation) {
        String oneLevelUp = parentLocation.getParent();

        return sourceComponents.stream()
                .map(component -> Paths.get(oneLevelUp, "test", component))
                .map(Path::toFile);
    }

    private Stream<File> sourceCodeLocation(File parentLocation) {
        return sourceComponents
                .stream()
                .map(component -> new File(parentLocation, component));
    }

    private LocalDateTime classCompileTime(File targetLocation) {
        Optional<File[]> timeStampFile = Optional.ofNullable(
                targetLocation.getParentFile().listFiles(this::isTimeStampFile));

        return timeStampFile
                .filter(x -> x.length > 0)
                .map(Arrays::asList)
                .map(this::mostRecentUpdateTime)
                .orElse(LocalDateTime.MIN)
                ;
    }

    private LocalDateTime mostRecentUpdateTime(List<File> files) {
        Stream<File> filesToCheck = files.stream()
                .peek(file -> info(String.format("Checking %s", file)))
                .filter(File::exists);

        Stream<Long> fileUpdateTimes = filesToCheck
                .flatMap(this::walkFile)
                .map(Path::toFile)
                .map(File::lastModified);

        Optional<Long> mostRecentTime = fileUpdateTimes.max(Long::compare);

        return mostRecentTime.map(this::toLocalDate).orElse(LocalDateTime.MIN);
    }

    private LocalDateTime toLocalDate(long value) {
        Instant epochValue = Instant.ofEpochMilli(value);
        return LocalDateTime.ofInstant(epochValue, ZoneId.systemDefault());
    }

    private Stream<Path> walkFile(File file) {
        try {
            return Files.walk(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isTimeStampFile(File file) {
        return file.getName().equalsIgnoreCase(TIMESTAMP_FILE);
    }


    private void info(String value) {
        getLog().info(value);
    }
}
