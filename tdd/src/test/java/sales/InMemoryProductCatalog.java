package sales;

import java.util.Map;

public class InMemoryProductCatalog {
    private final Map<String, String> barCodeToPrice;

    public InMemoryProductCatalog(Map<String, String> barCodeToPrice) {
        this.barCodeToPrice = barCodeToPrice;
    }

    public String findPrice(String barCode) {
        return barCodeToPrice.get(barCode);
    }
}
