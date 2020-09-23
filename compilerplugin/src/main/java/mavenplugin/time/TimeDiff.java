package mavenplugin.time;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TimeDiff {

    public static String diffAsString(LocalDateTime start, LocalDateTime end) {

        if (anyDateIsMin(start, end)) {
            return "";
        }

        Duration diff = Duration.between(start, end);

        String days = formatTimeValue(diff.toDays(), TimeUnit.DAYS);
        String hours = formatTimeValue(relativeHours(diff), TimeUnit.HOURS);
        String minutes = formatTimeValue(relativeMinutes(diff), TimeUnit.MINUTES);
        String seconds = formatTimeValue(relativeSeconds(diff), TimeUnit.SECONDS);

        return String.format("%s%s%s%s", days, hours, minutes, seconds);
    }

    private static boolean anyDateIsMin(LocalDateTime d1, LocalDateTime d2) {
        LocalDateTime min = LocalDateTime.MIN;
        return d1.equals(min) || d2.equals(min);
    }

    private static long relativeSeconds(Duration diff) {
        return (diff.toMillis() - TimeUnit.MILLISECONDS.convert(diff.toMinutes(), TimeUnit.MINUTES)) / 1000;
    }

    private static long relativeMinutes(Duration diff) {
        return diff.toMinutes() - TimeUnit.MINUTES.convert(diff.toHours(), TimeUnit.HOURS);
    }

    private static long relativeHours(Duration diff) {
        return diff.toHours() - TimeUnit.HOURS.convert(diff.toDays(), TimeUnit.DAYS);
    }

    private static String formatTimeValue(long value, TimeUnit unit) {
        return Optional.of(value)
                .filter(TimeDiff::gtZero)
                .map(h -> String.format("%s %s ", h, unit))
                .orElse("");
    }

    private static boolean gtZero(long value) {
        return value > 0;
    }

}
