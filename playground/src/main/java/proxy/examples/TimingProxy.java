package proxy.examples;

import proxy.BigCollection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public class TimingProxy implements InvocationHandler {
    private final Object realObject;

    public TimingProxy(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.nanoTime();
        try {
            return method.invoke(realObject, args);
        } finally {
            long total = System.nanoTime() - start;
            System.out.println(String.format("Function %s took %s nano seconds", method.getName(), total));
        }
    }

    public static <V> BigCollection<V> create(Supplier<BigCollection<V>> supplier) {
        return (BigCollection<V>) Proxy.newProxyInstance(BigCollection.class.getClassLoader(),
                new Class<?>[]{BigCollection.class},
                new TimingProxy(supplier.get()));
    }
}
