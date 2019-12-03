package pos;

import java.util.HashMap;
import java.util.Map;

public class MerchantStore {
    private final Display display;
    private Map<String, String> productPrice = new HashMap<String, String>() {
        {
            put("1234567", "10.99");
            put("234567", "11.99");
        }
    };

    public MerchantStore(Display display) {
        this.display = display;
    }

    public void onBarCode(String barCode) {
        if (barCode == null || barCode.trim().length() == 0) {
            this.display.setText("invalid scan");
        } else {

            if ("1234567".equals(barCode)) {
                this.display.setText(productPrice.get(barCode));
            } else if ("234567".equals(barCode)) {
                this.display.setText(productPrice.get(barCode));
            } else {
                this.display.setText("product not found " + barCode);
            }
        }
    }
}
