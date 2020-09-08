package stream.bookselfs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class MoreCollectorsOperators {

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
    class grouping_result {

        @Test
        public void group_books_by_topic() {
            Map<Topic, List<Book>> byTopic = library.stream()
                    .collect(Collectors.groupingBy(Book::getTopic));

            Assertions.assertEquals(2, byTopic.get(Topic.Fiction).size());
            Assertions.assertEquals(1, byTopic.get(Topic.Computing).size());
            Assertions.assertEquals(1, byTopic.get(Topic.Medicine).size());
        }
    }


}
