package pos;

import java.util.Map;
import java.util.Optional;

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

        Optional<String> priceAsText = productPrice(barCode);
        if (!priceAsText.isPresent()) {
            display.displayProductNotFund(barCode);
        } else {
            display.displayProductPrice(priceAsText.get());
        }

    }

    private Optional<String> productPrice(String barCode) {
        String priceAsText = productPrice.get(barCode);
        return Optional.ofNullable(priceAsText);
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }
}
