package proxy.fx.proxy;

import proxy.fx.ResultCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class StandInProcessingProxy implements InvocationHandler {
    private final Object realObject;
    private final ResultCache resultCache;

    public StandInProcessingProxy(Object realObject, ResultCache resultCache) {
        this.realObject = realObject;
        this.resultCache = resultCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        String key = prettyMethod(method, args);
        Object value = null;

        try {
            if (ThreadLocalRandom.current().nextBoolean()) {
                throw new RuntimeException("Failed");
            }
            value = method.invoke(realObject, args);
            resultCache.record(key, value);
        } catch (Exception e) {
            resultCache.requestFailed(key);
            Object cache = resultCache.get(key);
            if (cache != null) {
                return cache;
            }
        }
        return value;

    }

    public String paramsToString(Object[] args) {
        if (args == null) {
            return "";
        } else {
            return Arrays.stream(args).map(Object::toString).collect(Collectors.joining(","));
        }
    }

    public String prettyMethod(Method method, Object[] args) {
        return String.format("%s( %s )", method.getName(), paramsToString(args));
    }


}
