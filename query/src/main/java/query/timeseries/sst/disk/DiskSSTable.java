package query.timeseries.sst.disk;

import query.timeseries.sst.PageRecord;
import query.timeseries.sst.SortedStringTable;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

public class DiskSSTable<V> implements SortedStringTable<V> {
    private final SortedStringTable<V> underlyingTable;
    private final File storeLocation;
    private final String storeName;
    private final OrderedPageDirectory storePageDirectory;

    public DiskSSTable(SortedStringTable<V> underlyingTable, File storeLocation, String storeName,
                       Function<V, byte[]> toBytes,
                       Function<byte[],V> fromBytes) {
        this.underlyingTable = underlyingTable;
        this.storeLocation = storeLocation;
        this.storeName = storeName;
        Path dataStore = new File(storeLocation, String.format("%s.1", storeName)).toPath();
        this.storePageDirectory = OrderedPageDirectoryImpl.create(1024 * 8, dataStore);
    }

    @Override
    public void append(String key, V value) {
        underlyingTable.append(key, value);
    }

    @Override
    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        underlyingTable.iterate(from, to, consumer);
    }

    @Override
    public Collection<PageRecord<V>> buffers() {
        return underlyingTable.buffers();
    }

    @Override
    public void update(int pageId, PageRecord<V> page) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void remove(int pageId) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void flush() {
        //This function will DO IO

        //storePageDirectory.insert();

        buffers().forEach(c -> {

            storePageDirectory.insert(c.getPageInfo().getPageId(),
                    String.valueOf(c.getPageInfo().getMinValue()), String.valueOf(c.getPageInfo().getMaxValue()), new byte[0]);
        });
    }

}
