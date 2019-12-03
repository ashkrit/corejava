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
            displayInvalidScan();
            return;
        }

        if (productPrice.containsKey(barCode)) {
            displayProductPrice(barCode);
        } else {
            displayProductNotFund(barCode);
        }

    }

    private void displayProductNotFund(String barCode) {
        this.display.setText("product not found " + barCode);
    }

    private void displayProductPrice(String barCode) {
        this.display.setText(productPrice.get(barCode));
    }

    private void displayInvalidScan() {
        this.display.setText("invalid scan");
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }
}
