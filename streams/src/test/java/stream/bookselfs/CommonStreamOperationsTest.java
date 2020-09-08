package stream.bookselfs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("Stream frequently used operations")
public class CommonStreamOperationsTest {


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
    class one_to_one_transformation_like_map_filter {

        @Test
        public void all_book_titles() {

            List<String> titles = library
                    .stream()
                    .map(Book::getTitle)
                    .collect(Collectors.toList());

            List<String> expected = asList("Fundamental of Chinese fingernail image",
                    "Compiler: Principals, Techniques and Tools",
                    "Voss",
                    "Lord of the Rings");

            assertIterableEquals(expected, titles);


        }

        @Test
        public void find_computing_books() {

            List<Book> filterBooks = library
                    .stream()
                    .filter(book -> book.getTopic() == Topic.Computing)
                    .collect(Collectors.toList());

            assertEquals("Compiler: Principals, Techniques and Tools", filterBooks.get(0).title);

        }
    }


    @Nested
    class sorting_and_dedup {

        @Test
        public void sort_books_by_title() {

            List<Book> sortedBooks = library
                    .stream()
                    .sorted(Comparator.comparing(Book::getTitle))
                    .collect(Collectors.toList());

            assertEquals("Compiler: Principals, Techniques and Tools", sortedBooks.get(0).title);
        }


        @Test
        public void earliest_published_book() {

            Optional<Book> earliestBook = library
                    .stream()
                    .min(Comparator.comparing(Book::getPubDate));
            assertEquals("Lord of the Rings", earliestBook.get().title);
        }

        @Test
        public void books_sorted_by_number_of_authors() {

            List<String> expected = asList("Voss", "Lord of the Rings", "Fundamental of Chinese fingernail image", "Compiler: Principals, Techniques and Tools");

            List<String> allTitles = library
                    .stream()
                    .sorted(Comparator.comparing(Book::getAuthors,
                            Comparator.comparing(authors -> authors.size()))) // this is using comparing with 2 params ( Key,Comparator)
                    .map(Book::getTitle)
                    .collect(Collectors.toList());

            assertIterableEquals(expected, allTitles);

        }
    }

    @Nested
    class limiting_and_skipping {
        @Test
        public void top_2_books_sorted_by_title() {

            List<String> top2Books = library
                    .stream()
                    .sorted(Comparator.comparing(Book::getTitle))
                    .map(Book::getTitle)
                    .limit(2)
                    .collect(Collectors.toList());


            List<String> expected = asList("Compiler: Principals, Techniques and Tools", "Fundamental of Chinese fingernail image");
            assertEquals(expected, top2Books);
        }

        @Test
        public void skip_top_2_books_sorted_by_title() {

            List<String> nonTop2Books = library
                    .stream()
                    .sorted(Comparator.comparing(Book::getTitle))
                    .map(Book::getTitle)
                    .skip(2)
                    .collect(Collectors.toList());


            List<String> expected = asList("Lord of the Rings", "Voss");
            assertEquals(expected, nonTop2Books);
        }

    }

    @Nested
    class deduplication {
        @Test
        public void all_titles_in_library() {
            List<String> expected = asList("Fundamental of Chinese fingernail image", "Lord of the Rings", "Voss", "Compiler: Principals, Techniques and Tools");

            List<String> allTitles = new ArrayList<>(library
                    .stream()
                    .map(Book::getTitle)
                    .collect(Collectors.toSet()));

            Collections.sort(allTitles);
            Collections.sort(expected);

            assertIterableEquals(expected, allTitles);

        }

    }

    @Nested
    class primitive_streams {
        @Test
        public void total_number_of_authors() {

            int authorCount = library
                    .stream()
                    .mapToInt(b -> b.getAuthors().size()).sum();
            assertEquals(9, authorCount);
        }


    }

    @Nested
    class one_to_many_transformation {

        @Test
        public void all_authors_sorted_by_book_title() {

            List<String> sortedAuthors = library
                    .stream()
                    .sorted(Comparator.comparing(Book::getTitle))
                    .flatMap(book -> book.getAuthors().stream())//1 to Many
                    .distinct()
                    .collect(Collectors.toList());


            List<String> expected = asList("Aho", "Lam", "Sethi", "Ullman", "Li", "Fu", "Tolkien", "Patrick White");
            assertEquals(expected, sortedAuthors);
        }

        @Test
        public void total_number_of_pages() {

            int totalPages = library
                    .stream()
                    .flatMapToInt(b -> IntStream.of(b.pageCounts))
                    .sum();

            assertEquals(3_314, totalPages);
        }

    }

}
