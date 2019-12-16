package sales;

import org.junit.jupiter.api.Test;
import sales.catalog.ProductCatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract public class ProductCatalogSpecification {

    @Test
    public void product_found_cents() {
        Price cents = Price.cents(100);
        ProductCatalog catalog = makeProductCatalog("P1", cents);
        assertEquals(cents, catalog.findPrice("P1"));
    }

    @Test
    public void product_not_found() {
        ProductCatalog catalog = makeEmptyCatalog();
        assertEquals(null, catalog.findPrice("P1"));
    }

    abstract protected ProductCatalog makeEmptyCatalog();

    abstract protected ProductCatalog makeProductCatalog(String barCode, Price cents);
}
