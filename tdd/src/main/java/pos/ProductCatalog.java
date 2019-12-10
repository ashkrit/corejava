package pos;

import java.util.Map;
import java.util.Optional;

public class ProductCatalog {
    private Map<String, Integer> priceAsCents;

    public ProductCatalog(Map<String, Integer> priceAsCents) {
        this.priceAsCents = priceAsCents;
    }

    public Optional<Integer> productPriceCents(String barCode) {
        Integer priceAsCents = this.priceAsCents.get(barCode);
        return Optional.ofNullable(priceAsCents);
    }
}
