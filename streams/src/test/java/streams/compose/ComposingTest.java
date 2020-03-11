package streams.compose;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComposingTest {

    @Test
    public void compose_data_pipeline_single_point() {

        List<Point> points = asList(new Point(0, 10));
        Optional<Point> point = new ComposingExample().maxDistancePoint(points);
        assertEquals(new Point(0, 10), point.get());
    }

    @Test
    public void compose_data_pipeline_multiple_points() {

        List<Point> points = asList(new Point(0, 10), new Point(10, 10));
        Optional<Point> point = new ComposingExample().maxDistancePoint(points);
        assertEquals(new Point(10, 10), point.get());
    }


}
