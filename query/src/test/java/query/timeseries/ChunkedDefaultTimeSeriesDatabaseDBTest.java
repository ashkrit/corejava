package query.timeseries;

public class ChunkedDefaultTimeSeriesDatabaseDBTest extends TimeSeriesStoreContractTest {
    @Override
    void create() {
        db = TimeSeriesStore.memory(10);
    }
}
