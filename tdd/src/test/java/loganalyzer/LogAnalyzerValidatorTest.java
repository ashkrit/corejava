package loganalyzer;

import loganalyser.LogAnalyserBuilder;
import loganalyser.LogAnalyzer;
import loganalyser.validator.FileBasedExtensionValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Testing with real implementation
public class LogAnalyzerValidatorTest {


    LogAnalyzer logAnalyzer = new LogAnalyserBuilder().extensionValidator(new FileBasedExtensionValidator()).build();

    @DisplayName("Test for bad extension")
    @Test
    public void isvalid_for_bad_extension_returns_false() {

        Assertions.assertFalse(logAnalyzer.isValidLogFile("filewithbadextension.foo"));
    }


    @DisplayName("Test for good upper case extension")
    @Test
    public void isvalid_returns_true_when_extension_is_valid() {

        Assertions.assertTrue(logAnalyzer.isValidLogFile("filewithbadextension.SLF"));
    }

    @DisplayName("Test for good lower case extension")
    @Test
    public void isvalid_returns_true_when_extension_lower_case_is_valid() {
        Assertions.assertTrue(logAnalyzer.isValidLogFile("filewithbadextension.slf"));
    }


    @Test
    public void should_throw_exception_when_value_is_null() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> logAnalyzer.isValidLogFile(null));
    }

    @ParameterizedTest
    @ArgumentsSource(FileNamesArgumentProvider.class)
    public void should_test_multiple_files(String fileName, boolean expected) {
        assertEquals(expected, logAnalyzer.isValidLogFile(fileName));
    }


    static class FileNamesArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("filewithbadextension.slf", true),
                    Arguments.of("filewithbadextension.SLF", true),
                    Arguments.of("filewithbadextension.foo", false)
            );
        }
    }


}
