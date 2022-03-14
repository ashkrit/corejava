package proxy.examples;

import proxy.BigCollection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class AsyncProxy implements InvocationHandler {
    private final Object realObject;
    private ExecutorService es = Executors.newFixedThreadPool(1);

    public AsyncProxy(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        es.submit(() -> {
            try {
                System.out.println("Using thread " + Thread.currentThread().getName());
                method.invoke(realObject, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    public static <V> BigCollection<V> create(Supplier<BigCollection<V>> supplier) {
        return (BigCollection<V>) Proxy.newProxyInstance(BigCollection.class.getClassLoader(),
                new Class<?>[]{BigCollection.class},
                new AsyncProxy(supplier.get()));
    }
}
