package proxy.fx.proxy;

import proxy.fx.MethodTimingTracker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TimeRecorderProxy implements InvocationHandler {
    private final Object realObject;
    private final MethodTimingTracker tracker;

    public TimeRecorderProxy(Object realObject, MethodTimingTracker tracker) {
        this.realObject = realObject;
        this.tracker = tracker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long id = tracker.start(method, args);
        try {
            return method.invoke(realObject, args);
        } finally {
            tracker.end(id);
        }
    }


}
