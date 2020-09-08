package stream.bookselfs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

public class CollectorOperations {

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
    class single_value_collector {

        @Test
        public void single_value_on_integer_type() {


            assertEquals(10, createStream(5, 1, 2, 10).max().getAsInt());
            assertEquals(1, createStream(5, 1, 2, 10).min().getAsInt());
            assertEquals(18, createStream(5, 1, 2, 10).sum());
            assertEquals(4, createStream(5, 1, 2, 10).count());
            assertEquals(18 / 4, createStream(5, 1, 2, 10).count());


            IntSummaryStatistics summary = createStream(5, 1, 2, 10).summaryStatistics();
            assertEquals(1, summary.getMin());
            assertEquals(10, summary.getMax());
            assertEquals(18, summary.getSum());
            assertEquals(4, summary.getCount());
        }

        @Test
        public void book_page_count_stats() {
            IntSummaryStatistics summary = library.stream().flatMapToInt(b -> IntStream.of(b.pageCounts)).summaryStatistics();


            int minimumPages = 256;
            int totalPages = 3_314;
            int maximumPages = 1009;
            int noOfBooks = 6;

            assertEquals(noOfBooks, summary.getCount());
            assertEquals(totalPages, summary.getSum());
            assertEquals(minimumPages, summary.getMin());
            assertEquals(maximumPages, summary.getMax());
        }


        private IntStream createStream(int... ints) {
            return IntStream.of(ints);
        }

    }


    @Nested
    class non_number_values_collectors {

        @Test
        public void collect_as_list() {
            List<Book> sortedBooks = library
                    .stream()
                    .collect(toList());

            assertEquals("Fundamental of Chinese fingernail image", sortedBooks.get(0).title);
        }

        @Test
        public void collect_as_set() {
            Set<String> authors = library
                    .stream()
                    .map(Book::getAuthors)
                    .flatMap(au -> au.stream())
                    .collect(toSet());

            assertTrue(authors.contains("Sethi"));
        }

        @Test
        public void collect_as_set_of_different_type() {
            Collection<String> authors = library
                    .stream()
                    .map(Book::getAuthors)
                    .flatMap(au -> au.stream())
                    .collect(Collectors.toCollection(() -> new TreeSet<>()));

            assertTrue(authors.contains("Sethi"));
        }

        @Test
        public void collect_as_map() {
            Map<String, Year> authors = library
                    .stream()
                    .collect(toMap(Book::getTitle, Book::getPubDate));

            assertEquals(Year.of(2014), authors.get("Fundamental of Chinese fingernail image"));
            assertEquals(Year.of(2006), authors.get("Compiler: Principals, Techniques and Tools"));
        }


        @Test
        public void collect_as_map_of_different_type() {
            NavigableMap<String, Year> authors = library
                    .stream()
                    .collect(toMap(Book::getTitle, Book::getPubDate, (x, y) -> x, TreeMap::new));

            assertEquals(Year.of(2014), authors.get("Fundamental of Chinese fingernail image"));
            assertEquals(Year.of(2006), authors.get("Compiler: Principals, Techniques and Tools"));
        }


        @Test
        public void collect_as_map_fails_with_duplicate_keys() {

            List<Book> books = asList(
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2014), 25.2, Topic.Medicine),
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2016), 25.2, Topic.Medicine)
            );

            assertThrows(IllegalStateException.class, () -> {
                books
                        .stream()
                        .collect(toMap(Book::getTitle, Book::getPubDate));
            });

        }

        @Test
        public void collect_as_map_handle_duplicate_keys() {

            List<Book> books = asList(
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2014), 25.2, Topic.Medicine),
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2016), 25.2, Topic.Medicine)
            );

            Map<String, Year> authors = books
                    .stream()
                    .collect(toMap(Book::getTitle, Book::getPubDate, (y1, y2) -> y1.isAfter(y2) ? y1 : y2));

            assertEquals(Year.of(2016), authors.get("Fundamental of Chinese fingernail image"));

        }


    }

    @Nested
    class duplicate_key_collection {

        @Test
        public void collect_as_map_fails_with_duplicate_keys() {

            List<Book> books = asList(
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2014), 25.2, Topic.Medicine),
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2016), 25.2, Topic.Medicine)
            );

            assertThrows(IllegalStateException.class, () -> {
                books
                        .stream()
                        .collect(toMap(Book::getTitle, Book::getPubDate));
            });

        }

        @Test
        public void collect_as_map_handle_duplicate_keys() {

            List<Book> books = asList(
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2014), 25.2, Topic.Medicine),
                    new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2016), 25.2, Topic.Medicine)
            );

            Map<String, Year> authors = books
                    .stream()
                    .collect(toMap(Book::getTitle, Book::getPubDate, (y1, y2) -> y1.isAfter(y2) ? y1 : y2));

            assertEquals(Year.of(2016), authors.get("Fundamental of Chinese fingernail image"));

        }


    }

}
