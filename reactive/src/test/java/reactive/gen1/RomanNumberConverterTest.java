package reactive.gen1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RomanNumberConverterTest {

    static TreeMap<Integer, String> numberToRoman = new TreeMap<>() {{
        put(1000, "M");
        put(900, "CM");
        put(500, "D");
        put(400, "CD");
        put(100, "C");
        put(90, "XC");
        put(50, "L");
        put(40, "XL");
        put(10, "X");
        put(9, "IX");
        put(5, "V");
        put(4, "IV");
        put(1, "I");
    }};

    @Test
    public void generate_roman_number() {

        var numbers = Flux.range(1, 6);
        var romanNumbers = numbers
                .map(this::toRoman)
                .toStream()
                .collect(Collectors.toList());
        assertEquals(List.of("I", "II", "III", "IV", "V", "VI"), romanNumbers);
    }

    private String toRoman(int value) {
        var key = numberToRoman.floorKey(value);
        if (key == value) {
            return numberToRoman.get(value);
        }
        return numberToRoman.get(key) + toRoman(value - key);
    }
}
