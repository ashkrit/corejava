package query.timeseries;

import query.timeseries.impl.BasicTimeSeriesDatabase;
import query.timeseries.sst.InMemorySSTable;


public class ChunkedBasicTimeSeriesDatabaseDBTest extends TimeSeriesStoreContractTest {
    @Override
    void create() {
        db = new BasicTimeSeriesDatabase(new InMemorySSTable<>(10));
    }
}
