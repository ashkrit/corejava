package pos;

import java.util.Optional;

public class MerchantStore {
    private final Display display;
    private final ProductCatalog productCatalog;

    public MerchantStore(Display display, ProductCatalog productCatalog) {
        this.display = display;
        this.productCatalog = productCatalog;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            display.displayInvalidScan();
            return;
        }

        Optional<String> priceAsText = productCatalog.productPrice(barCode);
        if (!priceAsText.isPresent()) {
            display.displayProductNotFund(barCode);
        } else {
            display.displayProductPrice(priceAsText.get());
        }

    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }
}
