package pos;

import java.util.Map;

public class MerchantStore {
    private final DisplayDevice display;
    private Map<String, Double> productPrice;

    public MerchantStore(DisplayDevice display) {
        this.display = display;
    }

    public MerchantStore(Map<String, Double> productPrice, DisplayDevice display) {
        this.display = display;
        this.productPrice = productPrice;
    }

    public void onBarCode(String barCode) {
        if (barCode != null && barCode.trim().length() > 0) {

            if (productPrice != null) {
                Double price = productPrice.get(barCode);
                this.display.onMessage(String.format("$%.2f", price));
            } else {
                if (barCode.equals("123001")) {
                    this.display.onMessage("$10.99");
                } else {
                    this.display.onMessage("invalid barcode");
                }
            }
        }
    }
}
