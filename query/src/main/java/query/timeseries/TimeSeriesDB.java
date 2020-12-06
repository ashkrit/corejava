package query.timeseries;

import model.avro.EventInfo;

import java.util.function.Function;
import java.util.function.Supplier;

public interface TimeSeriesDB {

    <T> void register(Class<T> cls, Supplier<Function<Object, EventInfo>> fn);

    <T> EventInfo insert(T row);


}
