package pos;

import java.util.Map;
import java.util.Optional;

public class ProductCatalog {
    private Map<String, Integer> priceAsCents;
    private Map<String, String> priceAsText;

    @Deprecated
    public ProductCatalog(Map<String, String> productPrice) {
        this.priceAsText = productPrice;
    }

    public ProductCatalog(Map<String, String> priceAsText, Map<String, Integer> priceAsCents) {

        this.priceAsText = priceAsText;
        this.priceAsCents = priceAsCents;
    }

    public Optional<String> productPrice(String barCode) {
        String priceAsText = this.priceAsText.get(barCode);
        return Optional.ofNullable(priceAsText);
    }
}
