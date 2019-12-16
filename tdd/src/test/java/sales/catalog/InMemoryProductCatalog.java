package sales.catalog;

import sales.Price;

import java.util.Map;

public class InMemoryProductCatalog implements ProductCatalog {
    private final Map<String, Price> productPriceInCents;

    public InMemoryProductCatalog(Map<String, Price> productPriceInCents) {
        this.productPriceInCents = productPriceInCents;
    }


    @Override
    public Price findPriceAsCents(String barCode) {
        return productPriceInCents.get(barCode);
    }
}
