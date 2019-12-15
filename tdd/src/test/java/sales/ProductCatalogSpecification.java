package sales;

import org.junit.jupiter.api.Test;
import sales.catalog.ProductCatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract public class ProductCatalogSpecification {

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

    abstract protected ProductCatalog makeEmptyCatalog();

    abstract protected ProductCatalog makeProductCatalog(String barCode, float price);
}