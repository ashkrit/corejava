package testing.spec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static testing.spec.StringCommons.subStringBetween;

public class StringCommonsTest {

    @Test
    public void single_substring_with_single_char_open_close_tags() {
        assertArrayEquals(new String[]{"x"}, subStringBetween("axc", "a", "c"));
    }

    @Test
    public void many_substring_with_single_char_open_close_tags() {
        assertArrayEquals(new String[]{"x", "y", "z"}, subStringBetween("axcaycazc", "a", "c"));
    }

    @Test
    public void single_substring_with_multi_char_open_close_tags() {
        assertArrayEquals(new String[]{"xyz"}, subStringBetween("abxyzcd", "ab", "cd"));
    }

    @Test
    public void input_char_is_null() {
        assertAll(
                () -> assertArrayEquals(new String[]{}, subStringBetween("", "a", "c")),
                () -> assertArrayEquals(new String[]{}, subStringBetween(null, "b", "c"))
        );
    }

    @Test
    public void start_char_isnull_or_empty() {

        assertAll(
                () -> assertArrayEquals(new String[]{}, subStringBetween("xyz", null, "c")),
                () -> assertArrayEquals(new String[]{}, subStringBetween("xyz", "", "c"))
        );

    }

    @Test
    public void end_char_isnull_or_empty() {

        assertAll(
                () -> assertArrayEquals(new String[]{}, subStringBetween("xyz", "s", "")),
                () -> assertArrayEquals(new String[]{}, subStringBetween("xyz", "d", null))
        );

    }

    @Test
    public void no_match() {
        assertArrayEquals(new String[]{}, subStringBetween("axc", "c", "d"));
    }

    @Test
    public void zero_char_match() {
        assertArrayEquals(new String[]{}, subStringBetween("cd", "c", "d"));
    }


    @Test
    public void has_space() {
        assertArrayEquals(new String[]{" "}, subStringBetween("c d", "c", "d"));
    }

}
