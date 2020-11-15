package query;

import query.memory.InMemoryStore;
import query.persistent.mvstore.H2MVStore;
import query.persistent.rocks.RocksStore;

import java.io.File;

import static java.util.Arrays.stream;

public class KeyValueFactory {

    public static KeyValueStore create(String file, boolean reset) {
        System.out.println("Creating @ " + file);

        if (file.startsWith(InMemoryStore.type)) {
            return new InMemoryStore();
        } else if (file.startsWith(H2MVStore.type)) {
            File location = new File(file.replace(H2MVStore.type, ""));
            resetH2(reset, location);
            return new H2MVStore(location);
        } else if (file.startsWith(RocksStore.type)) {
            File location = new File(file.replace(RocksStore.type, ""));
            resetRocks(reset, location);
            return new RocksStore(location);
        }

        return null;
    }

    private static void resetRocks(boolean reset, File location) {
        if (reset) {
            stream(location.listFiles())
                    .filter(File::exists)
                    .forEach(File::delete);
        }
    }

    private static void resetH2(boolean reset, File location) {
        if (reset) {
            location
                    .getParentFile()
                    .mkdirs();
            if (location.exists()) {
                location.delete();
            }
        }
    }
}
