package context.impl;


import context.ContextProviderClient;
import context.DistributedContext;
import context.DistributedContextBuilder;

import java.util.Map;


public class MapProxy implements DistributedContext.DistributedMap {
    private final String name;
    private final ContextProviderClient client;
    private final DistributedContextBuilder.InternalDistributedContext context;

    public MapProxy(ContextProviderClient client, DistributedContextBuilder.InternalDistributedContext context, String name) {
        this.client = client;
        this.context = context;
        this.name = name;
    }

    @Override
    public String put(Map.Entry<String, String> entry) {
        return client.put(context.name, name, entry);
    }

    @Override
    public boolean put(Map.Entry<String, String> entry, String oldValue) {
        return client.casPut(context.name, name, entry, oldValue);
    }

    @Override
    public String get(String key) {
        return client.get(context.name, name, key);
    }

    @Override
    public String remove(String key) {
        return client.remove(context.name, name, key);
    }
}
