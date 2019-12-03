package pos;

import java.util.Map;

public class MerchantStore {
    private final Display display;
    private final Map<String, String> productPrice;

    public MerchantStore(Display display, Map<String, String> productToPrice) {
        this.display = display;
        this.productPrice = productToPrice;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            this.display.setText("invalid scan");
        } else {
            if (productPrice.containsKey(barCode)) {
                this.display.setText(productPrice.get(barCode));
            } else {
                this.display.setText("product not found " + barCode);
            }
        }
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }
}
