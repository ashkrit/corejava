package context;



import java.util.Map;

public interface ContextProviderClient {
    boolean createOrUseContext(String name, DistributedContextBuilder.InternalDistributedContext context);

    long longValue(String contextName, String variable);

    boolean longValueCas(String contextName, String variable, long current, long newValue);

    String get(String contextName, String mapName, String keyName);

    String remove(String contextName, String mapName, String keyName);

    String put(String contextName, String mapName, Map.Entry<String, String> entry);

    boolean casPut(String contextName, String mapName, Map.Entry<String, String> entry, String oldValue);
}
