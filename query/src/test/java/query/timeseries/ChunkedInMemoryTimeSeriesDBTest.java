package query.timeseries;

import query.timeseries.impl.InMemoryTimeSeries;


public class ChunkedInMemoryTimeSeriesDBTest extends TimeSeriesDBContractTest {
    @Override
    void create() {
        db = new InMemoryTimeSeries(new SSTable<>(10));
    }
}
