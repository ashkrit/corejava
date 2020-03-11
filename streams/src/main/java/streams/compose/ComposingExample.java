package streams.compose;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ComposingExample {
    public Optional<Point> maxDistancePoint(List<Point> points) {
        return points.stream().max(Comparator.comparingDouble(p -> p.distance(0, 0)));
    }

    public Optional<Point> fastMaxDistancePoint(List<Point> points) {
        return points.stream()
                .parallel() // Need just this and you fly
                .max(Comparator.comparingDouble(p -> p.distance(0, 0)));
    }
}
