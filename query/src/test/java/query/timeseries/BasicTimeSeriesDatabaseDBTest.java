package query.timeseries;


import query.timeseries.impl.BasicTimeSeriesDatabase;

public class BasicTimeSeriesDatabaseDBTest extends TimeSeriesStoreContractTest {

    @Override
    public void create() {
        db = new BasicTimeSeriesDatabase();
    }

}
