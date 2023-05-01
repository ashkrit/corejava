package context;

import java.util.Map;

public interface DistributedContext {
    DistributedNumber newAtomicLong(String name);

    DistributedMap newMap(String name);

    interface DistributedNumber {

        String name();

        long value();

        boolean cas(long current, long newValue);

    }

    interface DistributedMap {

        String put(Map.Entry<String, String> entry);

        boolean put(Map.Entry<String, String> entry, String oldValue);

        String get(String key);

        String remove(String key);

        default Map.Entry<String, String> create(String k, String v) {
            return new Map.Entry<String, String>() {
                @Override
                public String getKey() {
                    return k;
                }

                @Override
                public String getValue() {
                    return v;
                }

                @Override
                public String setValue(String value) {
                    throw new IllegalArgumentException("Not supported");
                }
            };
        }
    }

}
