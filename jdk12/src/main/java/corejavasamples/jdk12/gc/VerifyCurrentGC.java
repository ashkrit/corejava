package corejavasamples.jdk12.gc;

import java.lang.management.ManagementFactory;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.out;

/*
   Enable
   -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC
 */
public class VerifyCurrentGC {

    public static void main(String... args) {

        var gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        gcBeans.stream().forEach(gc -> {

            out.println(format("GC Name : %s", gc.getName()));
            var poolNames = gc.getMemoryPoolNames();
            if (poolNames != null) {
                List.of(poolNames).forEach(pool ->
                        out.println(format("Pool name %s", pool)));
            } else {
                out.println("No memory pools for " + gc.getName());
            }

        });

    }
}
