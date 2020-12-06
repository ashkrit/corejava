package query.timeseries;

import query.timeseries.impl.InMemoryTimeSeries;
import query.timeseries.sst.InMemorySSTable;


public class ChunkedInMemoryTimeSeriesDBTest extends TimeSeriesDBContractTest {
    @Override
    void create() {
        db = new InMemoryTimeSeries(new InMemorySSTable<>(10));
    }
}
