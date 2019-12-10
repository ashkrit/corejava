package pos;

import java.util.Optional;

public class MerchantStore {
    private final Display display;
    private final ProductCatalog productCatalog;
    private Optional<String> priceAsText = Optional.empty();

    public MerchantStore(Display display, ProductCatalog productCatalog) {
        this.display = display;
        this.productCatalog = productCatalog;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            display.displayInvalidScan();
            return;
        }

        priceAsText = productCatalog.productPrice(barCode);
        if (priceAsText.isPresent()) {
            display.displayProductPrice(formatPrice(priceAsText.get()));
        } else {
            display.displayProductNotFund(barCode);
        }

    }

    private String formatPrice(String priceAsText) {
        return priceAsText;
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }

    public void onTotal() {
        if (priceAsText.isPresent()) {
            display.displayTotal(formatPrice(priceAsText.get()));
        } else {
            display.noItemsSelected();
        }
    }
}
