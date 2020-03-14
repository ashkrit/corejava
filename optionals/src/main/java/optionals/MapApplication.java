package optionals;

public class MapApplication {

    public static void main(String[] args) {
        OptionalMap<String, String> keys = new OptionalHashMap<>();

        keys
                .getValue("key1")
                .ifPresent(value -> System.out.println("Found value " + value));
    }
}
