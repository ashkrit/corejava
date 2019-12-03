package pos;

import java.util.Map;
import java.util.Optional;

public class ProductCatalog {
    private final Map<String, String> productPrice;

    public ProductCatalog(Map<String, String> productPrice) {
        this.productPrice = productPrice;
    }

    public Optional<String> productPrice(String barCode) {
        String priceAsText = productPrice.get(barCode);
        return Optional.ofNullable(priceAsText);
    }
}
