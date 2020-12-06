package query.timeseries.impl;

import model.avro.EventInfo;
import query.timeseries.TimeSeriesDB;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class TimeSeriesDBImpl implements TimeSeriesDB {

    private final Map<Class, Supplier<Function<Object, EventInfo>>> eventBuilder = new ConcurrentHashMap<>();

    private final ClassValue<Function<Object, EventInfo>> classValue = new ClassValue<Function<Object, EventInfo>>() {
        @Override
        protected Function<Object, EventInfo> computeValue(Class type) {
            return eventBuilder.get(type).get();
        }
    };

    @Override
    public <T> void register(Class<T> cls, Supplier<Function<Object, EventInfo>> fn) {
        eventBuilder.put(cls, fn);
    }

    @Override
    public <T> EventInfo insert(T row) {

        Function<Object, EventInfo> fn = classValue.get(row.getClass());
        EventInfo event = fn.apply(row);
        return event;
    }
}
