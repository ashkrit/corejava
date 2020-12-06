package query.timeseries;

import org.junit.jupiter.api.Disabled;
import query.timeseries.impl.ChunkedInMemoryTimeSeries;

@Disabled
public class ChunkedInMemoryTimeSeriesDBTest extends TimeSeriesDBContractTest {
    @Override
    void create() {
        db = new ChunkedInMemoryTimeSeries();
    }
}
