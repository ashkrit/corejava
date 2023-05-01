package context.client;


import context.DistributedContext;
import context.DistributedContext.DistributedMap;
import context.DistributedContext.DistributedNumber;
import context.DistributedContextBuilder;
import context.impl.DBContextProviderClient;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static context.DistributedContextBuilder.createOrUse;


public class DistributedContextClient {

    public static void main(String[] args) throws SQLException {

        DistributedContextBuilder.client = new DBContextProviderClient("~/global_context");


        DistributedContext context = createOrUse("global", TimeUnit.MINUTES.toMinutes(10));


        DistributedNumber noOfRequests = context.newAtomicLong("noOfRequests");


        System.out.println(noOfRequests.name());
        System.out.println(noOfRequests.value());

        IntStream.range(0, 100)
                .parallel()
                .forEach($ -> {
                    long current = noOfRequests.value();
                    long newValue = current + 1;
                    boolean result = noOfRequests.cas(current, newValue);
                    System.out.println(Thread.currentThread() + ":" + result + "->" + current + " -> " + newValue);
                });

        System.out.println("Last Value " + noOfRequests.value());


        DistributedMap map = context.newMap("executionMetrics-123");
        map.put(map.create("job_started", LocalDateTime.now().toString()));
        map.put(map.create("job_completed", LocalDateTime.now().plusHours(5).toString()));

        System.out.println(map.get("job_started"));
        System.out.println(map.get("job_completed"));

    }
}
