package sales;

import sales.catalog.InMemoryProductCatalog;
import sales.catalog.ProductCatalog;

import java.util.Collections;

import static java.util.Collections.singletonMap;

public class InMemoryProductCatalogContractTest extends ProductCatalogSpecification {


    @Override
    protected ProductCatalog makeEmptyCatalog() {
        return new InMemoryProductCatalog(Collections.EMPTY_MAP);
    }

    @Override
    protected ProductCatalog makeProductCatalog(String barCode, Price cents) {
        return new InMemoryProductCatalog(singletonMap(barCode, cents));
    }
}
