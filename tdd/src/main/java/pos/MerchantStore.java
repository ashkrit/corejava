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
        if (isNotNull(barCode)) {
            Double price = productPrice.get(barCode);
            if (price != null) {
                this.display.onMessage(String.format("$%.2f", price));
            } else {
                this.display.onMessage("invalid barcode");
            }
        } else {
            this.display.onMessage("scan again");
        }
    }

    private boolean isNotNull(String barCode) {
        return barCode != null && barCode.trim().length() > 0;
    }
}
