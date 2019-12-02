package pos;

import java.util.Map;

public class PointOfSaleSystem {
    private final Map<String, Double> productPrice;
    private Double currentProduct;

    public PointOfSaleSystem(Map<String, Double> productPrice) {
        this.productPrice = productPrice;
    }

    public String message() {
        return "$10";
    }

    public void onBarCode(String barCode) {
        this.currentProduct = productPrice.get(barCode);
    }

    public String displayMessage() {
        return String.format("$%.2f", currentProduct);
    }
}
