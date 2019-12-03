package pos;

import java.util.Map;

public class MerchantStore {
    private final Display display;
    private final Map<String, String> productPrice;

    public MerchantStore(Display display, Map<String, String> productPrice) {
        this.display = display;
        this.productPrice = productPrice;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            display.displayInvalidScan();
            return;
        }

        String priceAsText = productPrice(barCode);
        if (priceAsText == null) {
            display.displayProductNotFund(barCode);
        } else {
            display.displayProductPrice(priceAsText);
        }

    }

    private String productPrice(String barCode) {
        return productPrice.get(barCode);
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }
}
