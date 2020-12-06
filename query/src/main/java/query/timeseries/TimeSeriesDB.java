package query.timeseries;

import model.avro.EventInfo;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

public interface TimeSeriesDB {

    <T> void register(Class<T> cls, Supplier<Function<Object, EventInfo>> fn);

    <T> EventInfo insert(T row);

    void from(LocalDateTime now, Function<EventInfo, Boolean> fn);
}
