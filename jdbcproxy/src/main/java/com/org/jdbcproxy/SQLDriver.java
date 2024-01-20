package com.org.jdbcproxy;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;
import java.util.function.Function;

public class SQLDriver implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public SQLDriver() {
        functions.put("toString", param -> this.toString());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // System.out.printf("Calling method %s(%s) \n", method.getName(), args);
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), $ -> null);
        return fn.apply(args);
    }

    public static Driver create() {
        return (Driver) Proxy.newProxyInstance(SQLDriver.class.getClassLoader(), new Class<?>[]{Driver.class}, new SQLDriver());
    }


    public static void register() {
        List<Driver> driverList = collectDrivers();

        driverList.forEach(driver -> MoreLang.safeExecuteV(() -> DriverManager.deregisterDriver(driver)));
        MoreLang.safeExecuteV(() -> DriverManager.registerDriver(create()));
        driverList.forEach(driver -> MoreLang.safeExecuteV(() -> DriverManager.registerDriver(driver)));

        collectDrivers();

    }

    private static List<Driver> collectDrivers() {
        int order = 0;
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        List<Driver> driverList = new ArrayList<>();
        while (drivers.hasMoreElements()) {
            Driver e = drivers.nextElement();
            driverList.add(e);
            System.out.printf("%s -> %s%n", order, e);
            order++;
        }
        return driverList;
    }

}
