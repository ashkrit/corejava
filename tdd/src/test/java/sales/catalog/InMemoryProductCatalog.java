package sales.catalog;

import sales.Price;

import java.util.Map;

public class InMemoryProductCatalog implements ProductCatalog {
    private Map<String, Price> productPriceInCents;
    private Map<String, Float> barCodeToPrice;

    public InMemoryProductCatalog(Map<String, Float> barCodeToPrice) {
        this.barCodeToPrice = barCodeToPrice;
    }

    public InMemoryProductCatalog(Map<String, Float> productPrice, Map<String, Price> productPriceInCents) {

        this.barCodeToPrice = productPrice;
        this.productPriceInCents = productPriceInCents;
    }

    @Override
    public Float findPrice(String barCode) {
        return barCodeToPrice.get(barCode);
    }

    @Override
    public Price findPriceAsCents(String barCode) {
        return productPriceInCents.get(barCode);
    }
}
