package query.timeseries.sst.disk;

import java.io.File;

public class StoreLocation {
    private final File root;
    private final String storeName;

    public StoreLocation(File root, String storeName) {
        this.root = root;
        this.storeName = storeName;
    }

    public File getRoot() {
        return root;
    }

    public String getStoreName() {
        return storeName;
    }
}
