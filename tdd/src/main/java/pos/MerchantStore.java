package pos;

import java.util.HashMap;
import java.util.Map;

public class MerchantStore {
    private final DisplayDevice display;
    private Map<String, Double> productPrice;

    public MerchantStore(DisplayDevice display) {
        this.display = display;
        this.productPrice = new HashMap<>();
    }

    public MerchantStore(Map<String, Double> productPrice, DisplayDevice display) {
        this.display = display;
        this.productPrice = productPrice;
    }

    public void onBarCode(String barCode) {
        String text;
        if (isNotNull(barCode)) {
            Double price = productPrice.get(barCode);
            text = priceToText(price);
        } else {
            text = "scan again";
        }

        this.display.onMessage(text);
    }

    private String priceToText(Double price) {
        String text;
        if (price != null) {
            text = String.format("$%.2f", price);
        } else {
            text = "invalid barcode";
        }
        return text;
    }

    private boolean isNotNull(String barCode) {
        return barCode != null && barCode.trim().length() > 0;
    }
}
