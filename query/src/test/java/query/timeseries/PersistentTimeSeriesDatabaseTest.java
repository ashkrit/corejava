package query.timeseries;

import model.avro.EventInfo;
import query.timeseries.sst.disk.PersistentSSTable;
import query.timeseries.sst.disk.RecordSerializer;
import query.timeseries.sst.disk.StoreLocation;
import query.timeseries.sst.memory.InMemorySSTable;

import java.io.File;
import java.util.Arrays;

public class PersistentTimeSeriesDatabaseTest extends TimeSeriesStoreContractTest {
    @Override
    void create() {
        File storeLocation = new File(System.getProperty("java.io.tmpdir"), "events");
        storeLocation.mkdirs();
        Arrays.stream(storeLocation.listFiles()).forEach(File::delete);

        RecordSerializer<EventInfo> serializer = new RecordSerializer<>(1024, null, null, null);
        StoreLocation location = new StoreLocation(storeLocation, "taxi_events" + System.nanoTime());
        db = TimeSeriesStore.persistence(new PersistentSSTable<>(new InMemorySSTable<>(10), location, serializer));
    }
}
