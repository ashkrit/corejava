package playground;

public class Main {

    static {

        //System.loadLibrary("lib_rust");
        System.load("/Users/ashkrit/_code/github/corejava/explore/explore/liblib_rust.dylib");
    }
    public static native int doubleRust(int input);

    public static native String ping(String input);

    public static void main(String[] args) {

        var result = Main.doubleRust(20);
        System.out.println(result);

        var result2 = Main.ping("ping minus 2");
        System.out.println(result2);
    }
}
