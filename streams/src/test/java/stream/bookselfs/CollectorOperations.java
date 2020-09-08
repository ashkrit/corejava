package stream.bookselfs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

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


            IntSummaryStatistics intSummaryStatistics = createStream(5, 1, 2, 10).summaryStatistics();
            assertEquals(1, intSummaryStatistics.getMin());
            assertEquals(10, intSummaryStatistics.getMax());
            assertEquals(18, intSummaryStatistics.getSum());
            assertEquals(18 / 4, intSummaryStatistics.getAverage());
            assertEquals(4, intSummaryStatistics.getCount());
        }


        private IntStream createStream(int... ints) {
            return IntStream.of(ints);
        }

    }

}
