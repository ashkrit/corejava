package pos;

import java.util.Map;

public class PointOfSaleSystem {
    private final Map<String, Double> productPrice;
    private Double currentProduct;

    public PointOfSaleSystem(Map<String, Double> productPrice) {
        this.productPrice = productPrice;
    }

    public String message() {
        return displayMessage();
    }

    public void onBarCode(String barCode) {
        this.currentProduct = productPrice.get(barCode);
    }

    public String displayMessage() {
        return currentProduct == null ? "-1" : String.format("$%.2f", currentProduct);
    }
}
