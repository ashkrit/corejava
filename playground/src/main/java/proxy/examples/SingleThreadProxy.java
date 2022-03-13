package proxy.examples;

import proxy.BigCollection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public class SingleThreadProxy implements InvocationHandler {
    private final Object realObject;

    public SingleThreadProxy(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        synchronized (realObject) {
            return method.invoke(realObject, args);
        }
    }

    public static <V> BigCollection<V> create(Supplier<BigCollection<V>> supplier) {
        return (BigCollection<V>) Proxy.newProxyInstance(BigCollection.class.getClassLoader(),
                new Class<?>[]{BigCollection.class},
                new SingleThreadProxy(supplier.get()));
    }
}
