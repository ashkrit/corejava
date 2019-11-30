import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//Testing with Stub + Mock
public class LogAnalyserErrorTest {

    @Test
    public void should_send_email_when_unable_log_error_message() {

        StringBuilder subjectText = new StringBuilder();
        LogAnalyzer analyzer = new LogAnalyserBuilder()
                .errorService(message -> {
                    throw new RuntimeException("Service is down");
                })
                .emailService(subject -> subjectText.append(subject))
                .build();


        analyzer.analyze("small.t");
        String expected = "Service is down";
        Assertions.assertEquals(expected, subjectText.toString());

    }

}
