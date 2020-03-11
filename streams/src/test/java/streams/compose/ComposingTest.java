package streams.compose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComposingTest {

    @Test
    public void single_point() {

        List<Point> points = asList(new Point(0, 10));
        Optional<Point> point = new ComposingExample().maxDistancePoint(points);
        assertEquals(new Point(0, 10), point.get());
    }

    @Test
    public void multiple_points() {

        List<Point> points = asList(new Point(0, 10), new Point(10, 10));
        Optional<Point> point = new ComposingExample().maxDistancePoint(points);
        assertEquals(new Point(10, 10), point.get());
    }


    @Test
    public void multiple_points_parallel() {

        List<Point> points = asList(new Point(0, 10), new Point(10, 10));
        Optional<Point> point = new ComposingExample().fastMaxDistancePoint(points);
        assertEquals(new Point(10, 10), point.get());
    }

    @Test
    public void all_points_in_sorted_order() {

        List<Point> points = asList(new Point(10, 10), new Point(0, 10));
        List<Point> sortedPoint = new ComposingExample().sort(points);

        assertEquals(new Point(0, 10), sortedPoint.get(0));
        assertEquals(new Point(10, 10), sortedPoint.get(1));

    }


}
