package proxy;

public class CollectionApp {

    public static void main(String[] args) {

        BigCollection<String> collection = new BigCollectionProxy<>(AwsCollection::new);

        collection.add("Value1");
        collection.add("Value2");

        collection.forEach(System.out::println);

        System.out.println("Exists " + collection.exists("Value2"));


    }
}
