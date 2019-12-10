package pos;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ProductCatalog {
    private Map<String, Integer> priceAsCents;

    public ProductCatalog(Map<String, Integer> priceAsCents) {
        this.priceAsCents = priceAsCents;
    }

    public Optional<Integer> priceAsCents(String barCode) {
        Integer priceAsCents = this.priceAsCents.get(barCode);
        return ofNullable(priceAsCents);
    }
}
