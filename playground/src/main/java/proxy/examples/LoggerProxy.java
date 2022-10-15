package proxy.examples;

import proxy.BigCollection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public class LoggerProxy implements InvocationHandler {
    private final Object realObject;

    public LoggerProxy(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            System.out.println(String.format("Calling -> %s", method.getName()));
            return method.invoke(realObject, args);
        } finally {
            System.out.println(String.format("Done -> %s", method.getName()));
        }
    }

    public static <V> BigCollection<V> create(Supplier<BigCollection<V>> supplier) {
        return (BigCollection<V>) Proxy.newProxyInstance(BigCollection.class.getClassLoader(),
                new Class<?>[]{BigCollection.class},
                new LoggerProxy(supplier.get()));
    }
}
