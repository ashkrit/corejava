package query.timeseries;

import query.timeseries.impl.DefaultTimeSeriesDatabase;
import query.timeseries.sst.memory.InMemorySSTable;


public class ChunkedDefaultTimeSeriesDatabaseDBTest extends TimeSeriesStoreContractTest {
    @Override
    void create() {
        db = TimeSeriesStore.create(10);
    }
}
