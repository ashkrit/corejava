package reactive.gen0;

import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Generation0App {

    public static void main(String[] args) throws InterruptedException {

        var stream = new DataObservable("NumberStream");

        var pool = Executors.newScheduledThreadPool(1);
        var numberStream = IntStream.range(0, 10).iterator();
        pool.scheduleAtFixedRate(() -> {
            if (numberStream.hasNext()) {
                stream.onNext(numberStream.next());
            }
        }, 0, 1, TimeUnit.SECONDS);

        stream.addObserver((o, value) -> System.out.println(String.format("First %s -> %s", o, value)));
        Thread.sleep(5000);
        stream.addObserver((o, value) -> System.out.println(String.format("Second %s -> %s", o, value)));

    }

    static class DataObservable extends Observable {
        private final String name;

        DataObservable(String name) {
            this.name = name;
        }

        public void onNext(Object value) {
            this.setChanged();
            this.notifyObservers(value);
        }

        @Override
        public String toString() {
            return String.format("Observable[%s]", name);
        }
    }
}
