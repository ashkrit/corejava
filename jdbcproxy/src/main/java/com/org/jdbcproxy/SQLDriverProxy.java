package com.org.jdbcproxy;

import com.org.jdbcproxy.SQLFactory.SQLObjects;
import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;
import java.util.function.Function;

public class SQLDriverProxy implements InvocationHandler {

    public static final String JDBC_PROXY_KEY = "jdbc/proxy/key=";
    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();
    private final String driverUrl;

    public SQLDriverProxy(String driverUrl) {
        this.driverUrl = driverUrl;
        functions.put("toString", param -> this.toString());
        functions.put("connect", this::_connect);
    }

    private Connection _connect(Object[] param) {
        int index = 0;
        String url = (String) param[index++];
        Properties properties = (Properties) param[index++];

        if (!url.startsWith(driverUrl)) {
            return null;
        }

        String updatedUrl = url.replace(driverUrl, "");

        SQLObjects sqlObjects = _search(updatedUrl);

        return sqlObjects.connection.apply(updatedUrl);

    }

    private static SQLObjects _search(String connectionString) {
        System.out.println("Params " + connectionString);
        return SQLFactory.search(connectionString);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), $ -> null);
        return fn.apply(args);
    }

    public static Driver create() {
        return (Driver) Proxy.newProxyInstance(SQLDriverProxy.class.getClassLoader(), new Class<?>[]{Driver.class},
                new SQLDriverProxy(JDBC_PROXY_KEY));
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
            order++;
        }
        return driverList;
    }

}
