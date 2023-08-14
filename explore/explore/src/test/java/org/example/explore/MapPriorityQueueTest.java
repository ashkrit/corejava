package org.example.explore;

import org.example.explore.ds.MapPriorityQueue;
import org.example.explore.ds.Treep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapPriorityQueueTest {

    List<Product> items = Arrays.asList(
            Product.of("AXION Yellow", 2.12f, .10f),
            Product.of("Meji Fresh Milk 2L", 6.9f, 0.0f),
            Product.of("red Chilli 100 G", 1.14f, .05f),
            Product.of("Fresh Cucumber", 1.37f, .01f),
            Product.of("China Garlic", 1.93f, 0.0f),
            Product.of("Red Onion", 1.19f, 0.07f),
            Product.of("Fuji Apple", 3.14f, .11f),
            Product.of("Banana", 3.58f, .12f)
    );

    @Test
    public void lookup_by_product_name() {

        Treep<String, Product> stores = new MapPriorityQueue<>(Product::name, Collections.emptyMap());
        items.forEach(stores::add);

        assertEquals(Product.of("Red Onion", 1.19f, 0.07f), stores.get("red onion"));

    }

    record Product(String name, float price, float discount) {


        public static Product of(String name, float price, float discount) {
            return new Product(name.toLowerCase(), price, discount);
        }
    }
}
