package mavenplugin;

import mavenplugin.io.IOFunctions;
import mavenplugin.time.TimeDiff;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Mojo(name = "inc", defaultPhase = LifecyclePhase.PRE_CLEAN)
public class IncrementalMojo extends AbstractMojo {

    private static final String TIMESTAMP_FILE = "buildcheck.timestamp";
    private static final List<String> sourceComponents = Arrays.asList("java", "scala", "resources");

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

        info("Code compiled at %s", codeCompileAt);
        info("Code changed at %s", codeChangedAt);

        if (codeChangedAt.isAfter(codeCompileAt)) {
            prepareForCompilation(outputDirectory, codeChangedAt, codeCompileAt);
        } else {
            nothingToClean(codeChangedAt);
        }
    }

    private void prepareForCompilation(File targetLocation, LocalDateTime codeChangedAt, LocalDateTime codeCompileAt) {

        info("Code was changed %s after compilation", TimeDiff.diffAsString(codeCompileAt, codeChangedAt));
        Path rootTarget = targetLocation.getParentFile().toPath();
        info("Changed detected - cleaning %s", rootTarget);

        cleanTargetLocation(rootTarget);
        createTimeStampFile(rootTarget);

    }

    private void cleanTargetLocation(Path rootTarget) {
        Stream.of(rootTarget)
                .filter(Files::exists)
                .forEach(IOFunctions::deleteFiles);
    }

    private void createTimeStampFile(Path rootTarget) {
        rootTarget.toFile().mkdir();
        Path timeStampFile = new File(rootTarget.toFile(), TIMESTAMP_FILE).toPath();
        IOFunctions.touch(timeStampFile);
    }

    private void nothingToClean(LocalDateTime codeChangedAt) {
        String s = TimeDiff.diffAsString(codeChangedAt, LocalDateTime.now());
        info("Nothing to clean - Source and target are up to date. Not updated from %s", s);
        project.getProperties().setProperty("skipTests", "true");
    }

    private LocalDateTime codeChangeTime(List<String> compileSourceRoots) {

        Stream<File> javaSourceLocation = compileSourceRoots.stream()
                .filter(this::isJavaLocation)
                .map(File::new);

        Stream<File> rootSourceLocation = javaSourceLocation.map(File::getParentFile);

        List<File> resourceToScan = rootSourceLocation
                .flatMap(this::sourceLocations)
                .filter(File::exists)
                .collect(Collectors.toList());

        return mostRecentUpdateTime(resourceToScan);
    }

    private boolean isJavaLocation(String location) {
        return location.endsWith("java");
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
        return Stream.of(Paths.get(twoLevelUp, "pom.xml"))
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
        File[] matchedFile = targetLocation.getParentFile().listFiles(this::isTimeStampFile);
        Optional<File[]> timeStampFile = ofNullable(matchedFile);

        return timeStampFile
                .filter(this::hasFile)
                .map(Arrays::asList)
                .map(this::mostRecentUpdateTime)
                .orElse(LocalDateTime.MIN)
                ;
    }

    private boolean hasFile(File[] x) {
        return x.length > 0;
    }

    private LocalDateTime mostRecentUpdateTime(List<File> files) {
        Stream<File> filesToCheck = files.stream()
                .peek(file -> info("Checking %s", file))
                .filter(File::exists);

        Stream<File> fileUpdateTimes = filesToCheck
                .flatMap(IOFunctions::walkFile)
                .map(Path::toFile);

        Optional<File> mostRecentTime = fileUpdateTimes.max(Comparator.comparingLong(File::lastModified));
        mostRecentTime.ifPresent(f -> info("Last changed file/folder is %s", f));

        return mostRecentTime.map(File::lastModified)
                .map(this::toLocalDate)
                .orElse(LocalDateTime.MIN);
    }

    private LocalDateTime toLocalDate(long value) {
        Instant epochValue = Instant.ofEpochMilli(value);
        return LocalDateTime.ofInstant(epochValue, ZoneId.systemDefault());
    }

    private boolean isTimeStampFile(File file) {
        return file.getName().equalsIgnoreCase(TIMESTAMP_FILE);
    }

    private void info(String template, Object... args) {
        getLog().info(String.format(template, args));
    }
}
