package corejavasamples.jdk12.gc;

/*
JEP 318: Epsilon: A No-Op Garbage Collector
    Reference - https://openjdk.java.net/jeps/318

    -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xlog:gc -Xmx1g

 */
public class MemoryAllocator {

    public static final int KB = 1024;
    static int mbToAllocate = Integer.getInteger("mb", 1000);

    public static void main(String[] args) {
        System.out.println(String.format("Start allocation of %s MBs", mbToAllocate));

        for (var i = 0; i < mbToAllocate; i++) {
            var garbage = new byte[KB * KB];
        }

        System.out.println("I was Alive after allocation");
    }
}