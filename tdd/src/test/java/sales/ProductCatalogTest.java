package sales;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductCatalogTest {

    @Test
    public void product_found() {

        InMemoryProductCatalog catalog = new InMemoryProductCatalog(Collections.singletonMap("P1", "100"));
        assertEquals("100", catalog.findPrice("P1"));
    }

    @Test
    public void product_not_found() {
        InMemoryProductCatalog catalog = new InMemoryProductCatalog(Collections.EMPTY_MAP);
        assertEquals(null, catalog.findPrice("P1"));
    }
}
