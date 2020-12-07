package query.page;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

public class ApplicationClock {

    public static Supplier<Long> provider = () -> System.currentTimeMillis();

    public static long now() {
        return provider.get();
    }

    public static LocalDateTime fromTs(long ts) {
        return ofInstant(ofEpochMilli(ts), systemDefault());
    }

}
