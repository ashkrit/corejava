package stream.bookselfs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class BookSelfTest {


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

    @Test
    public void sort_books_by_title() {

        List<Book> sortedBooks = library
                .stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());

        assertEquals("Compiler: Principals, Techniques and Tools", sortedBooks.get(0).title);
    }

    @Test
    public void all_authors_sorted_by_book_title() {

        List<String> sortedAuthors = library
                .stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .flatMap(book -> book.getAuthors().stream())
                .distinct()
                .collect(Collectors.toList());


        List<String> expected = asList("Aho", "Lam", "Sethi", "Ullman", "Li", "Fu", "Tolkien", "Patrick White");
        assertEquals(expected, sortedAuthors);
    }


    @Test
    public void top_2_books_sorted_by_title() {

        List<String> sortedAuthors = library
                .stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .map(Book::getTitle)
                .limit(2)
                .collect(Collectors.toList());


        List<String> expected = asList("Compiler: Principals, Techniques and Tools", "Fundamental of Chinese fingernail image");
        assertEquals(expected, sortedAuthors);
    }
}
