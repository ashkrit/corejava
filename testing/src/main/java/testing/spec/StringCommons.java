package testing.spec;

import java.util.ArrayList;
import java.util.List;

/**
 * searches for substrings between two tags in a given string and returns all the matching substrings
 */
public class StringCommons {
    private static final String[] EMPTY = {};

    /**
     * @param str      - null, empty , any random string > 1
     * @param openTag  - null , empty, single char , multi char
     * @param closeTag - null , empty, single char , multi char
     * @return
     */
    public static String[] subStringBetween(String str, String openTag, String closeTag) {

        if (isNullOrEmpty(str) || isNullOrEmpty(openTag) || isNullOrEmpty(closeTag)) {
            return EMPTY;
        }

        List<String> words = new ArrayList<>();
        for (int index = 0; index < str.length(); ) {

            int openTagIndex = str.indexOf(openTag, index);
            if (openTagIndex == -1) break;


            index = openTagIndex + openTag.length();
            int closeTagIndex = str.indexOf(closeTag, index);
            if (closeTagIndex > -1) {
                String part = str.substring(index, closeTagIndex);
                if (!part.isEmpty()) {
                    words.add(part);
                }
                index = closeTagIndex + closeTag.length();
            }


        }

        return words.toArray(EMPTY);
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
