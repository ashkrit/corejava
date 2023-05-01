package context.impl;


import context.ContextProviderClient;
import context.DistributedContext;
import context.DistributedContextBuilder;

public class NumberProxy implements DistributedContext.DistributedNumber {
    private final DistributedContextBuilder.InternalDistributedContext context;
    private final String name;

    private final ContextProviderClient client;

    public NumberProxy(ContextProviderClient client, DistributedContextBuilder.InternalDistributedContext context, String name) {

        this.context = context;
        this.name = name;
        this.client = client;
    }

    @Override
    public String name() {
        return String.format("%s/%s", context.name, name);
    }

    @Override
    public long value() {
        return client.longValue(context.name, name);
    }

    @Override
    public boolean cas(long current, long newValue) {
        return client.longValueCas(context.name, name, current, newValue);
    }
}
