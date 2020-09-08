package stream.bookselfs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchOperationsTest {


    private List<Book> library;

    @BeforeEach
    public void loadBooks() {
        this.library = asList(
                new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2014), 25.2, Topic.Medicine),
                new Book("Compiler: Principals, Techniques and Tools", asList("Aho", "Lam", "Sethi", "Ullman"), new int[]{1009}, Year.of(2006), 23.6, Topic.Computing),
                new Book("Voss", asList("Patrick White"), new int[]{478}, Year.of(1957), 19.8, Topic.Fiction),
                new Book("Lord of the Rings", asList("Tolkien"), new int[]{531, 416, 624}, Year.of(1955), 23.0, Topic.Fiction)
        );
    }

    @Nested
    class matching_operations {

        @Test
        public void match_all_and_return_count() {
            long count = library
                    .stream()
                    .filter(book -> book.topic == Topic.Computing) // Applied to every element
                    .count();
            assertEquals(1, count);
        }

        @Test
        public void match_some_values() {
            boolean match = library
                    .stream()
                    .anyMatch(book -> book.topic == Topic.Computing); // true if any element match, this will be only applied on some element

            assertEquals(true, match);
        }

        @Test
        public void match_all_values() {
            boolean match = library
                    .stream()
                    .allMatch(book -> book.topic == Topic.Computing); // true if all element matches

            assertEquals(false, match);
        }


        @Test
        public void nothing_should_match() {
            boolean match = library
                    .stream()
                    .noneMatch(book -> book.topic == Topic.History); // true if none of the element matches

            assertEquals(true, match);
        }

    }


}
