package proxy.fx;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MethodTimingTracker {

    private final ConcurrentSkipListMap<String, MethodExecutionTime> executionTime = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    private final AtomicLong sequence = new AtomicLong();

    public long start(Method method, Object[] args) {
        long id = sequence.incrementAndGet();
        executionTime.put(String.valueOf(id), new MethodExecutionTime(method.getName(), args, System.nanoTime()));
        return id;
    }

    public void end(long id) {
        long end = System.nanoTime();
        String tempId = String.valueOf(id);
        MethodExecutionTime current = executionTime.remove(tempId);

        current.setEndTimeNs(end);

        String key = String.format("%s/%s", pad(current.elapsed(), 10), id);
        executionTime.put(key, current);

    }

    public void dumpSlowRequests(int limit) {
        executionTime.values()
                .stream()
                .filter(MethodExecutionTime::isComplete)
                .limit(limit)
                .forEach(m -> {
                    System.out.printf("Method %s took %s ms%n", m.prettyMethod(), TimeUnit.NANOSECONDS.toMillis(m.elapsed()));
                });
    }


    public static class MethodExecutionTime {
        private final String name;
        private final Object[] args;
        private final long startTimeNs;
        private long endTimeNs;

        MethodExecutionTime(String name, Object[] args, long startTimeNs) {
            this.name = name;
            this.args = args;
            this.startTimeNs = startTimeNs;
        }

        public void setEndTimeNs(long endTimeNs) {
            this.endTimeNs = endTimeNs;
        }

        public long elapsed() {
            return endTimeNs - startTimeNs;
        }

        public String paramsToString() {
            if (args == null) {
                return "";
            } else {
                return Arrays.stream(args).map(Object::toString).collect(Collectors.joining(","));
            }
        }

        public String prettyMethod() {
            return String.format("%s( %s )", name, paramsToString());
        }

        public boolean isComplete() {
            return endTimeNs > 0;
        }
    }

    private String pad(long value, int pad) {
        String time = String.valueOf(value);
        int padLength = Math.max(pad - time.length(), 0);
        String padValue = IntStream.range(0, padLength).mapToObj(x -> "0").collect(Collectors.joining());
        return padValue + time;
    }

}
