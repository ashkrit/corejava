package pos;

import java.util.Map;
import java.util.Optional;

public class PointOfSaleSystem {
    public static final String MISSING_PRICE = "-1";
    private final Map<String, Double> productPrice;

    private Optional<Double> currentProduct;

    public PointOfSaleSystem(Map<String, Double> productPrice) {
        this.productPrice = productPrice;
    }

    public void onBarCode(String barCode) {
        this.currentProduct = lookupPrice(barCode);
    }

    private Optional<Double> lookupPrice(String barCode) {
        Double price = productPrice.get(barCode);
        return price == null ? Optional.empty() : Optional.of(price);
    }

    public String displayMessage() {
        return currentProduct
                .map(price -> String.format("$%.2f", price))
                .orElse(MISSING_PRICE);
    }
}
