package query.timeseries.impl;

import model.avro.EventInfo;
import query.timeseries.sst.memory.InMemorySSTable;
import query.timeseries.sst.SortedStringTable;
import query.timeseries.TimeSeriesStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultTimeSeriesDatabase implements TimeSeriesStore {

    private final Map<Class, Supplier<Function<Object, EventInfo>>> eventBuilder = new ConcurrentHashMap<>();
    private final ClassValue<Function<Object, EventInfo>> classValue = new ClassValue<Function<Object, EventInfo>>() {
        @Override
        protected Function<Object, EventInfo> computeValue(Class type) {
            return eventBuilder.get(type).get();
        }
    };

    private final DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final SortedStringTable<EventInfo> ssTable;

    public DefaultTimeSeriesDatabase(SortedStringTable<EventInfo> ssTable) {
        this.ssTable = ssTable;
    }

    public DefaultTimeSeriesDatabase() {
        this(new InMemorySSTable<>(Integer.MAX_VALUE));
    }

    @Override
    public <T> void register(Class<T> cls, Supplier<Function<Object, EventInfo>> fn) {
        eventBuilder.put(cls, fn);
    }

    @Override
    public <T> EventInfo insert(T row) {
        Function<Object, EventInfo> fn = classValue.get(row.getClass());
        EventInfo event = fn.apply(row);
        ssTable.append(event.getEventTime().toString(), event);
        return event;
    }

    @Override
    public void gt(LocalDateTime fromTime, Function<EventInfo, Boolean> consumer) {
        ssTable.iterate(fromTime.format(f), null, consumer);
    }

    @Override
    public void lt(LocalDateTime toTime, Function<EventInfo, Boolean> consumer) {
        ssTable.iterate(null, toTime.format(f), consumer);
    }

    @Override
    public void between(LocalDateTime startTime, LocalDateTime endTime, Function<EventInfo, Boolean> consumer) {

        String startKey = startTime.format(f);
        String endKey = endTime.format(f);
        ssTable.iterate(startKey, endKey, consumer);
    }

    @Override
    public void flush() {
        this.ssTable.flush();
    }

}
