package streams.compose;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ComposingTest {

    @Test
    public void compose_data_pipeline() {

        List<Point> points = Arrays.asList(new Point(0, 10));

        Point point = new ComposingExample().maxDistancePoint(points);

        Assertions.assertEquals(new Point(0, 10), point);


    }
}
