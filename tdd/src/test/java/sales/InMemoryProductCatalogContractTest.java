package sales;

import sales.catalog.InMemoryProductCatalog;
import sales.catalog.ProductCatalog;

import java.util.Collections;

public class InMemoryProductCatalogContractTest extends ProductCatalogSpecification {


    @Override
    protected ProductCatalog makeEmptyCatalog() {
        return new InMemoryProductCatalog(Collections.EMPTY_MAP);
    }

    @Override
    protected ProductCatalog makeProductCatalog(String barCode, float price) {
        return new InMemoryProductCatalog(Collections.singletonMap(barCode, price));
    }
}
