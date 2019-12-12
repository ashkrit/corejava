package sales;

import java.util.Map;

public class InMemoryProductCatalog {
    private final Map<String, Float> barCodeToPrice;

    public InMemoryProductCatalog(Map<String, Float> barCodeToPrice) {
        this.barCodeToPrice = barCodeToPrice;
    }

    public Float findPrice(String barCode) {
        return barCodeToPrice.get(barCode);
    }
}
