package query.timeseries;

import model.avro.EventInfo;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TimeSeriesStore {

    <T> void register(Class<T> cls, Supplier<Function<Object, EventInfo>> fn);

    <T> EventInfo insert(T row);

    void gt(LocalDateTime fromTime, Function<EventInfo, Boolean> consumer);

    void lt(LocalDateTime toTime, Function<EventInfo, Boolean> consumer);

    void between(LocalDateTime startTime, LocalDateTime endTime, Function<EventInfo, Boolean> consumer);
}
