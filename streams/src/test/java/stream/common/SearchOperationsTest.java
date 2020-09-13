package stream.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import stream.bookselfs.Book;
import stream.bookselfs.Topic;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        public void count_computing_books() {
            long count = library
                    .stream()
                    .filter(book -> book.topic == Topic.Computing) // Applied to every element
                    .count();
            assertEquals(1, count);
        }

    }

    @Nested
    class matching_operations_that_end_streams_and_returns_boolean {

        @Test
        public void has_any_computing_book() {
            boolean match = library
                    .stream()
                    .anyMatch(book -> book.topic == Topic.Computing); // true if any element match, this will be only applied on some element

            assertEquals(true, match);
        }

        @Test
        public void only_contains_computing_books() {
            boolean match = library
                    .stream()
                    .allMatch(book -> book.topic == Topic.Computing); // true if all element matches

            assertEquals(false, match);
        }


        @Test
        public void no_history_books() {
            boolean match = library
                    .stream()
                    .noneMatch(book -> book.topic == Topic.History); // true if none of the element matches

            assertEquals(true, match);
        }

        @Test
        public void any_fiction_book_published_in_1955() {
            Year publishYear = Year.of(1955);
            boolean match = library
                    .stream()
                    .filter(book -> book.topic == Topic.Fiction)
                    .anyMatch(book -> book.pubDate.equals(publishYear));

            assertEquals(true, match);
        }
    }

    @Nested
    class ending_match_on_empty_stream {
        List<Book> noBooks = Collections.emptyList();

        @Test
        public void all_match_is_true_on_empty_collection() {
            boolean match = noBooks
                    .stream()
                    .allMatch(book -> book.topic == Topic.Computing); // Always true for empty collection
            assertEquals(true, match);
        }


        @Test
        public void no_history_books() {
            boolean match = noBooks
                    .stream()
                    .noneMatch(book -> book.topic == Topic.History);
            assertEquals(true, match);
        }

        @Test
        public void has_any_computing_book() {
            boolean match = noBooks
                    .stream()
                    .anyMatch(book -> book.topic == Topic.Computing);
            assertEquals(false, match);
        }


        @Test
        public void any_fiction_book_published_in_1955() {
            Year publishYear = Year.of(1955);
            boolean match = noBooks
                    .stream()
                    .filter(book -> book.topic == Topic.Fiction)
                    .anyMatch(book -> book.pubDate.equals(publishYear));

            assertEquals(false, match);
        }
    }


    @Nested
    class matching_operations_that_ends_streams_and_return_record {

        @Test
        public void has_any_computing_book() {
            Optional<Book> match = library
                    .stream()
                    .filter(book -> book.topic == Topic.Computing)
                    .findAny();

            assertEquals("Compiler: Principals, Techniques and Tools", match.get().title);
        }

        @Test
        public void only_contains_computing_books() {
            Optional<Book> match = library
                    .stream()
                    .filter(book -> book.topic == Topic.Fiction)
                    .findFirst(); // Only required when order is important

            assertEquals("Voss", match.get().title);
        }


    }


}
