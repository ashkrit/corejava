package proxy.fx;

import proxy.fx.impl.FXServiceAPI;
import proxy.fx.proxy.StandInProcessingProxy;
import proxy.fx.proxy.TimeRecorderProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class MethodTrackingApp {

    public static void main(String[] args) {


        MethodTimingTracker tracker = new MethodTimingTracker();
        ResultCache cache = new ResultCache();

        FXServiceAPI core = new FXServiceAPI("https://api.exchangerate.host", 1);

        FXService timeRecorderProxy = create(FXService.class, new TimeRecorderProxy(core, tracker));
        FXService standInProxy = create(FXService.class, new StandInProcessingProxy(timeRecorderProxy, cache));

        FXService fx = standInProxy;


        List<String> currency = new ArrayList<String>() {{
            add("USD");
            add("INR");
            add("GBP");
            add("IDR");
            add("JPY");
            add("CAD");
        }};

        IntStream.range(0, 100).forEach($ -> {
            Collections.shuffle(currency);
            currency.parallelStream().forEach(code -> {
                try {
                    Double d = fx.convert("SGD", code, 1);
                    System.out.println(d);
                } catch (Exception e) {
                    System.out.println("Failed for " + code);
                }
            });
        });

        tracker.dumpSlowRequests(10);
        cache.prettyPrint();

    }


    public static <T> T create(Class<T> interfaceType, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(MethodTrackingApp.class.getClassLoader(), new Class<?>[]{interfaceType}, handler);
    }


}
