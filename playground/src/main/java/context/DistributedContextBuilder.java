package context;


import context.impl.InMemoryContextProviderClient;
import context.impl.MapProxy;
import context.impl.NumberProxy;

public class DistributedContextBuilder {


    public static ContextProviderClient client = new InMemoryContextProviderClient();


    public static DistributedContext createOrUse(String name, long ttlInMinutes) {

        InternalDistributedContext context = new InternalDistributedContext(name, ttlInMinutes);
        client.createOrUseContext(name, context);
        return new DistributedContext() {
            @Override
            public DistributedNumber newAtomicLong(String name) {
                return new NumberProxy(client, context, name);
            }

            @Override
            public DistributedMap newMap(String name) {
                return new MapProxy(client, context, name);
            }
        };
    }


    public static class InternalDistributedContext {

        public final String name;
        public final long ttlInMinutes;

        public InternalDistributedContext(String name, long ttlInMinutes) {
            this.name = name;
            this.ttlInMinutes = ttlInMinutes;
        }
    }
}
