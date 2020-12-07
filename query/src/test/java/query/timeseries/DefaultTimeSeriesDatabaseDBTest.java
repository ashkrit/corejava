package query.timeseries;


public class DefaultTimeSeriesDatabaseDBTest extends TimeSeriesStoreContractTest {

    @Override
    public void create() {
        db = TimeSeriesStore.memory();
    }

}
