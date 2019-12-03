package pos;

import java.util.HashMap;
import java.util.Map;

public class MerchantStore {
    public static final String ERROR_INVALID_BARCODE = "invalid barcode";
    public static final String ERROR_SCAN_AGAIN = "scan again";

    private final DisplayDevice display;
    private final Map<String, Double> productPrice;

    public MerchantStore(DisplayDevice display) {
        this(new HashMap<>(), display);
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
            text = ERROR_SCAN_AGAIN;
        }

        this.display.onMessage(text);
    }

    private String priceToText(Double price) {
        String text;
        if (price != null) {
            text = String.format("$%.2f", price);
        } else {
            text = ERROR_INVALID_BARCODE;
        }
        return text;
    }

    private boolean isNotNull(String barCode) {
        return barCode != null && barCode.trim().length() > 0;
    }
}
