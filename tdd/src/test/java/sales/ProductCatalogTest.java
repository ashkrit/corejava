package sales;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductCatalogTest {

    @Test
    public void product_found() {
        ProductCatalog catalog = makeProductCatalog("P1", 100);
        assertEquals(100, catalog.findPrice("P1"));
    }

    @Test
    public void product_not_found() {
        ProductCatalog catalog = makeEmptyCatalog();
        assertEquals(null, catalog.findPrice("P1"));
    }

    private ProductCatalog makeEmptyCatalog() {
        return new InMemoryProductCatalog(Collections.EMPTY_MAP);
    }

    private ProductCatalog makeProductCatalog(String barCode, float price) {
        return new InMemoryProductCatalog(Collections.singletonMap(barCode, price));
    }
}
