package com.org.jdbcproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SQLConnectionProxy implements InvocationHandler {

    private final Object target;

    public SQLConnectionProxy(Object target) {
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target, args);
    }
}
