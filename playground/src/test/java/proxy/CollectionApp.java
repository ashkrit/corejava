package proxy;

import proxy.examples.AsyncProxy;

public class CollectionApp {

    public static void main(String[] args) {

        BigCollection<String> collection = AsyncProxy.create(AwsCollection::new);

        collection.add("Value1");
        collection.add("Value2");

        collection.forEach(System.out::println);

        System.out.println(collection.getClass());


    }
}
