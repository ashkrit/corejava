package proxy;

import proxy.examples.BigCollectionTimingProxy;

public class CollectionApp {

    public static void main(String[] args) {

        BigCollection<String> collection = BigCollectionTimingProxy.create(AwsCollection::new);

        collection.add("Value1");
        collection.add("Value2");

        collection.forEach(System.out::println);

        System.out.println("Exists " + collection.exists("Value2"));

    }
}
