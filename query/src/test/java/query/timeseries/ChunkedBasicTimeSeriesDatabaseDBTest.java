package query.timeseries;

import query.timeseries.impl.BasicTimeSeriesDatabase;
import query.timeseries.sst.DiskSSTable;
import query.timeseries.sst.InMemorySSTable;


public class ChunkedBasicTimeSeriesDatabaseDBTest extends TimeSeriesStoreContractTest {
    @Override
    void create() {
        db = new BasicTimeSeriesDatabase(new DiskSSTable(new InMemorySSTable<>(10)));
    }
}