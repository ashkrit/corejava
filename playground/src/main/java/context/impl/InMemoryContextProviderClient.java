package context.impl;



import context.ContextProviderClient;
import context.DistributedContextBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryContextProviderClient implements ContextProviderClient {

    private static ConcurrentMap<String, DistributedContextBuilder.InternalDistributedContext> contexts = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, AtomicLong> numbers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> maps = new ConcurrentHashMap<>();

    @Override
    public boolean createOrUseContext(String name, DistributedContextBuilder.InternalDistributedContext context) {
        InMemoryContextProviderClient.contexts.put(name, context);
        return true;
    }

    @Override
    public long longValue(String contextName, String variable) {
        String key = String.format("%s/%s", contextName, variable);
        return numbers.getOrDefault(key, new AtomicLong(0)).get();

    }

    @Override
    public boolean longValueCas(String contextName, String variable, long current, long newValue) {
        String key = String.format("%s/%s", contextName, variable);
        numbers.putIfAbsent(key, new AtomicLong(0));
        return numbers.get(key).compareAndSet(current, newValue);

    }

    @Override
    public String get(String contextName, String mapName, String keyName) {

        String key = String.format("%s/%s/%s", contextName, mapName, keyName);
        return maps.get(key);
    }

    @Override
    public String remove(String contextName, String mapName, String keyName) {

        String key = String.format("%s/%s/%s", contextName, mapName, keyName);
        return maps.remove(key);

    }

    @Override
    public String put(String contextName, String mapName, Map.Entry<String, String> entry) {
        String key = String.format("%s/%s/%s", contextName, mapName, entry.getKey());
        return maps.put(key, entry.getValue());
    }

    @Override
    public boolean casPut(String contextName, String mapName, Map.Entry<String, String> entry, String oldValue) {
        String key = String.format("%s/%s/%s", contextName, mapName, entry.getKey());
        return maps.replace(key, oldValue, entry.getValue());
    }
}
