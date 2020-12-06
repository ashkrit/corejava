package query.timeseries;


import query.timeseries.impl.InMemoryTimeSeries;

public class InMemoryTimeSeriesDBTest extends TimeSeriesDBContractTest {

    @Override
    public void create() {
        db = new InMemoryTimeSeries();
    }

}
