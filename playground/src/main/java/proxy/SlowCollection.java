package proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SlowCollection<V> implements BigCollection<V> {
    private List<V> values = new ArrayList<>();
    private final int delayTimeInMs;

    public SlowCollection(int delayTimeInMs) {
        this.delayTimeInMs = delayTimeInMs;
    }

    @Override
    public void add(V value) {
        delay();
        values.add(value);
    }

    private void delay() {
        int sleepTime = 10 + new Random().nextInt(delayTimeInMs);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean exists(V value) {
        delay();
        return values.contains(value);
    }

    @Override
    public void forEach(Consumer<V> c) {
        delay();
        values.forEach(c::accept);
    }
}
