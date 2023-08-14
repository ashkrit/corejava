package org.example.explore;

import org.example.explore.ds.MapPriorityQueue;
import org.example.explore.ds.Treep;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapPriorityQueueTest {

    @Test
    public void lookup_by_product_name() {

        Treep<String, Product> stores = new MapPriorityQueue<>(Product::name, Collections.emptyMap());
        Arrays.asList(
                Product.of("AXION Yellow", 2.12f, .10f),
                Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f),
                Product.of("red Chilli 100 G", 1.14f, .05f),
                Product.of("Fresh Cucumber", 1.37f, .01f),
                Product.of("China Garlic", 1.93f, 0.0f),
                Product.of("Red Onion", 1.19f, 0.07f),
                Product.of("Fuji Apple", 3.14f, .11f),
                Product.of("Banana", 3.58f, .12f)
        ).forEach(stores::add);

        assertEquals(Product.of("Red Onion", 1.19f, 0.07f), stores.get("red onion"));

    }


    @Test
    public void peek_at_top_discounted_product() {

        Map<String, Comparator<Product>> orderCols = Map.of(
                "discount", Comparator.comparing(Product::discount).reversed()
        );
        Treep<String, Product> stores = new MapPriorityQueue<>(Product::name, orderCols);


        Arrays.asList(
                Product.of("AXION Yellow", 2.12f, .10f),
                Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f),
                Product.of("red Chilli 100 G", 1.14f, .05f),
                Product.of("Fresh Cucumber", 1.37f, .01f),
                Product.of("China Garlic", 1.93f, 0.0f),
                Product.of("Red Onion", 1.19f, 0.07f),
                Product.of("Fuji Apple", 3.14f, .11f),
                Product.of("Banana", 3.58f, .12f)
        ).forEach(stores::add);

        assertEquals(Product.of("Banana", 3.58f, .12f), stores.top("discount"));

    }

    @Test
    public void delete_top_discounted_product() {

        Map<String, Comparator<Product>> orderCols = Map.of(
                "discount", Comparator.comparing(Product::discount).reversed()
        );
        Treep<String, Product> stores = new MapPriorityQueue<>(Product::name, orderCols);


        Arrays.asList(
                Product.of("AXION Yellow", 2.12f, .10f),
                Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f),
                Product.of("red Chilli 100 G", 1.14f, .05f),
                Product.of("Fresh Cucumber", 1.37f, .01f),
                Product.of("China Garlic", 1.93f, 0.0f),
                Product.of("Red Onion", 1.19f, 0.07f),
                Product.of("Fuji Apple", 3.14f, .11f),
                Product.of("Banana", 3.58f, .12f)
        ).forEach(stores::add);

        Product product = stores.top("discount");
        stores.delete(product.name);

        assertEquals( Product.of("Fuji Apple", 3.14f, .11f), stores.top("discount"));

    }

    @Test
    public void remove_top_discounted_products() {

        Map<String, Comparator<Product>> orderCols = Map.of(
                "discount", Comparator.comparing(Product::discount).reversed()
        );
        Treep<String, Product> stores = new MapPriorityQueue<>(Product::name, orderCols);


        Arrays.asList(
                Product.of("AXION Yellow", 2.12f, .10f),
                Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f),
                Product.of("red Chilli 100 G", 1.14f, .05f),
                Product.of("Fresh Cucumber", 1.37f, .01f),
                Product.of("China Garlic", 1.93f, 0.0f),
                Product.of("Red Onion", 1.19f, 0.07f),
                Product.of("Fuji Apple", 3.14f, .11f),
                Product.of("Banana", 3.58f, .12f)
        ).forEach(stores::add);

        Product first = stores.takeTop("discount");
        Product second = stores.takeTop("discount");
        assertEquals(Product.of("Banana", 3.58f, .12f), first);
        assertEquals(Product.of("Fuji Apple", 3.14f, .11f), second);

    }

    @Test
    public void peek_at_top_product_by_discount_and_price() {

        Treep<String, Product> stores = new MapPriorityQueue<>(Product::name, Map.of(
                "discount", Comparator.comparing(Product::discount).reversed(),
                "price", Comparator.comparing(Product::price).reversed()
        ));


        Arrays.asList(
                Product.of("AXION Yellow", 2.12f, .10f),
                Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f),
                Product.of("red Chilli 100 G", 1.14f, .05f),
                Product.of("Fresh Cucumber", 1.37f, .01f),
                Product.of("China Garlic", 1.93f, 0.0f),
                Product.of("Red Onion", 1.19f, 0.07f),
                Product.of("Fuji Apple", 3.14f, .11f),
                Product.of("Banana", 3.58f, .12f)
        ).forEach(stores::add);

        assertEquals(Product.of("Banana", 3.58f, .12f), stores.top("discount"));
        assertEquals(Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f), stores.top("price"));

    }



    record Product(String name, float price, float discount) {


        public static Product of(String name, float price, float discount) {
            return new Product(name.toLowerCase(), price, discount);
        }
    }
}
