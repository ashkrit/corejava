package sales.catalog;

import java.util.Map;

public class InMemoryProductCatalog implements ProductCatalog {
    private final Map<String, Float> barCodeToPrice;

    public InMemoryProductCatalog(Map<String, Float> barCodeToPrice) {
        this.barCodeToPrice = barCodeToPrice;
    }

    @Override
    public Float findPrice(String barCode) {
        return barCodeToPrice.get(barCode);
    }
}
