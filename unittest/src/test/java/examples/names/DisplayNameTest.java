package examples.names;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Now you can give good test name")
public class DisplayNameTest {

    @Test
    @DisplayName("Meaningful name test")
    public void should_use_good_names() {

    }

    @Test
    @DisplayName("😱")
    void now_you_can_have_emoji_in_name() {
    }
}
