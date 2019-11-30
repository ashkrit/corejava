import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import validator.FileBasedExtensionValidator;

//Testing with real implementation + Mock
public class LogAnalyzerAnalysisTest {


    @Test
    public void should_record_error_message_for_short_file() {

        StringBuilder sb = new StringBuilder();
        LogAnalyzer analyzer = new LogAnalyserBuilder()
                .extensionValidator(new FileBasedExtensionValidator())
                .errorService(message -> sb.append(message)).build();

        analyzer.analyze("a.txt");
        String expected = "file name must be >= 10";
        Assertions.assertEquals(expected, sb.toString());
    }

    @Test
    public void should_not_log_error_message_when_file_name_is_long() {

        StringBuilder sb = new StringBuilder();
        LogAnalyzer analyzer = new LogAnalyserBuilder()
                .extensionValidator(new FileBasedExtensionValidator())
                .errorService(message -> sb.append(message)).build();

        analyzer.analyze("verylongfilename.txt");
        Assertions.assertTrue(sb.length() == 0);
    }
}
