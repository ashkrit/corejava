package stream.collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import stream.bookselfs.Book;
import stream.bookselfs.Topic;

import java.time.Year;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
    Collector composition is very powerful, it allow to create nw collectors by combining many collector.

    [....] -> Collector(Classification, Downstream collector)

    [....] -> groupingBy(bookCategory,toList())  // Put element in list
    [...] ->  groupingBy(bookCategory,maxBy(books.author.size())) // Pick book with max number of author
    [...] ->  groupingBy(bookCategory,counting) //Count number of books by category
    [...] ->  groupingBy(bookCategory,summingInt(books.author.size) //Sum number of author
    [...] ->  groupingBy(bookCategory,mapping(bookName , collector)) // collect after mapping value

 */
public class CollectorsCompositionOperators {

    private List<Book> library;

    @BeforeEach
    public void loadBooks() {
        this.library = asList(
                new Book("Fundamental of Chinese fingernail image", asList("Li", "Fu", "Li"), new int[]{256}, Year.of(2014), 25.2, Topic.Medicine),
                new Book("Compiler: Principals, Techniques and Tools", asList("Aho", "Lam", "Sethi", "Ullman"), new int[]{1009}, Year.of(2006), 23.6, Topic.Computing),
                new Book("Voss", asList("Patrick White"), new int[]{478}, Year.of(1957), 19.8, Topic.Fiction),
                new Book("Lord of the Rings", asList("Tolkien"), new int[]{531, 416, 624}, Year.of(1955), 23.0, Topic.Fiction),
                new Book("The Cinderella Murder", asList("Mary Higgins Clark", "Alafair Burke"), new int[]{600}, Year.of(2014), 23.0, Topic.Fiction),
                new Book("Warren The 13th", asList("Tania Del Rio"), new int[]{237}, Year.of(2017), 15.0, Topic.Fiction)
        );
    }

    @Nested
    class grouping_result {

        @Test
        public void group_books_by_topic() {

            Map<Topic, List<Book>> byTopic = library.stream()
                    .collect(groupingBy(Book::getTopic));

            assertEquals(4, byTopic.get(Topic.Fiction).size());
            assertEquals(1, byTopic.get(Topic.Computing).size());
            assertEquals(1, byTopic.get(Topic.Medicine).size());
        }


        @Test
        public void group_books_by_topic_using_explicit_downstream_collector() {
            Map<Topic, List<Book>> byTopic = library.stream()
                    .collect(groupingBy(Book::getTopic, Collectors.toList()));
            assertEquals(4, byTopic.get(Topic.Fiction).size());
            assertEquals(1, byTopic.get(Topic.Computing).size());
            assertEquals(1, byTopic.get(Topic.Medicine).size());
        }

        @Test
        public void group_books_by_title_to_year_order_by_title() {


            List<Book> books = asList(
                    new Book("Compiler: Principals, Techniques and Tools", asList("Aho", "Lam", "Sethi", "Ullman"), new int[]{1009}, Year.of(2018), 23.6, Topic.Computing),
                    new Book("Compiler: Principals, Techniques and Tools", asList("Aho", "Lam", "Sethi", "Ullman"), new int[]{1009}, Year.of(2006), 23.6, Topic.Computing)
            );

            SortedMap<String, Year> byTopic = books.stream()
                    .collect(toMap(Book::getTitle, Book::getPubDate,
                            BinaryOperator.maxBy(Comparator.naturalOrder()),
                            TreeMap::new));

            assertEquals(Year.of(2018), byTopic.get("Compiler: Principals, Techniques and Tools"));
        }

        @Test
        public void topic_book_with_maximum_number_of_authors() {
            Map<Topic, Optional<Book>> byTopic = library.stream()
                    .collect(groupingBy(Book::getTopic,
                            maxBy(Comparator.comparing(b -> b.getAuthors().size()))));

            assertEquals("The Cinderella Murder", byTopic.get(Topic.Fiction).get().title);
        }


        @Test
        public void topic_with_pages_count() {
            Map<Topic, Integer> byTopic = library.stream()
                    .collect(groupingBy(Book::getTopic, summingInt(b -> b.getPageCounts().length)));

            assertEquals(6, byTopic.get(Topic.Fiction));
            assertEquals(1, byTopic.get(Topic.Computing));
            assertEquals(1, byTopic.get(Topic.Medicine));
        }


        @Test
        public void topic_with_maximum_books() {

            // Book -> (Topic,1) -> (Topic,sum)  |  max(Topic.sum) -> Topic

            //Pipeline 1  - Topic to book count mapping
            Map<Topic, Long> topicToBookCount = library
                    .stream()
                    .collect(groupingBy(Book::getTopic, counting()));

            //Pipeline 2  - Picking topic with max count value
            Optional<Topic> topTopic = topicToBookCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey);

            assertEquals(Topic.Fiction, topTopic.get());
        }

        @Test
        public void topic_with_all_the_books() {

            Collector<Book, ?, String> mapping = mapping(Book::getTitle, joining(";"));

            Map<Topic, String> topicBooks = library
                    .stream()
                    .collect(groupingBy(Book::getTopic, mapping));
            assertEquals("Voss;Lord of the Rings;The Cinderella Murder;Warren The 13th", topicBooks.get(Topic.Fiction));

        }


    }

    @Nested
    class partition_result {

        @Test
        public void partition_result_by_fiction() {
            Map<Boolean, List<Book>> byTopic = library.stream()
                    .collect(partitioningBy(b -> b.getTopic() == Topic.Fiction));

            assertEquals(4, byTopic.get(true).size());
            assertEquals(2, byTopic.get(false).size());
        }
    }
}
