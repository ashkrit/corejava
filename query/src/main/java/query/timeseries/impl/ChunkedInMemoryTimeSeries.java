package query.timeseries.impl;

import model.avro.EventInfo;
import query.timeseries.TimeSeriesDB;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChunkedInMemoryTimeSeries implements TimeSeriesDB {

    private final Map<Class, Supplier<Function<Object, EventInfo>>> eventBuilder = new ConcurrentHashMap<>();
    private final ClassValue<Function<Object, EventInfo>> classValue = new ClassValue<Function<Object, EventInfo>>() {
        @Override
        protected Function<Object, EventInfo> computeValue(Class type) {
            return eventBuilder.get(type).get();
        }
    };

    @Override
    public <T> void register(Class<T> cls, Supplier<Function<Object, EventInfo>> fn) {

    }

    @Override
    public <T> EventInfo insert(T row) {
        return null;
    }

    @Override
    public void gt(LocalDateTime fromTime, Function<EventInfo, Boolean> consumer) {

    }

    @Override
    public void lt(LocalDateTime toTime, Function<EventInfo, Boolean> consumer) {

    }

    @Override
    public void between(LocalDateTime startTime, LocalDateTime endTime, Function<EventInfo, Boolean> consumer) {

    }
}
