package query.timeseries;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class SystemTimeIdGenerator implements EventIdGenerator {
    private final AtomicLong counter = new AtomicLong();
    private final DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final int roll;

    public SystemTimeIdGenerator(int roll) {
        this.roll = roll;
    }

    @Override
    public String next() {
        String dateTime = LocalDateTime.now().format(f);
        return toId(dateTime);
    }


    @Override
    public String next(long ms) {
        return toId(fromMillSeconds(ms).format(f));
    }

    private String toId(String dateTime) {
        return dateTime + "/" + nextSequence();
    }
    private int nextSequence() {
        return (int) (counter.incrementAndGet() % roll);
    }

    private static LocalDateTime fromMillSeconds(long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }


}
