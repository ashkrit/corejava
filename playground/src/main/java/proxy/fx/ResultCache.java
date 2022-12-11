package proxy.fx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ResultCache {
    private final Map<String, Object> methodReply = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedFrequency = new ConcurrentHashMap<>();
    private final AtomicLong itemCounter = new AtomicLong();
    private final AtomicLong hitCounter = new AtomicLong();
    private final AtomicLong missCounter = new AtomicLong();

    private final AtomicLong failedCounter = new AtomicLong();

    public void record(String key, Object value) {
        methodReply.put(key, value);
        itemCounter.incrementAndGet();
    }

    public Object get(String key) {
        Object value = methodReply.get(key);
        if (value == null) {
            missCounter.incrementAndGet();
        } else {
            hitCounter.incrementAndGet();
        }
        return value;
    }

    public long hits() {
        return hitCounter.get();
    }


    public long miss() {
        return missCounter.get();
    }

    public void prettyPrint() {
        System.out.printf("Items %s , Fail %s , Hits %s , Miss %s \n", itemCounter.get(), failedCounter.get(), hitCounter, missCounter);
        System.out.println(failedFrequency);
    }

    public void requestFailed(String key) {
        failedCounter.incrementAndGet();
        failedFrequency.computeIfPresent(key, (k, v) -> v + 1);
        failedFrequency.putIfAbsent(key, 1);

    }

}
