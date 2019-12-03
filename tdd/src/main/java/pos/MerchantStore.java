package pos;

import java.util.HashMap;
import java.util.Map;

public class MerchantStore {
    private final Display display;
    private final Map<String, String> productPrice = new HashMap<String, String>() {
        {
            put("1234567", "10.99");
            put("234567", "11.99");
        }
    };

    public MerchantStore(Display display) {
        this.display = display;
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
