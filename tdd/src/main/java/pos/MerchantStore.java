package pos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class MerchantStore {

    private final Display display;
    private final ProductCatalog productCatalog;
    private Collection<Integer> productPrices = new ArrayList<>();

    public MerchantStore(Display display, ProductCatalog productCatalog) {
        this.display = display;
        this.productCatalog = productCatalog;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            display.displayInvalidScan();
            return;
        }

        Optional<Integer> priceInCents = productCatalog.productPriceCents(barCode);
        if (priceInCents.isPresent()) {
            display.displayProductPrice(priceInCents.get());
            productPrices.add(priceInCents.get());
        } else {
            display.displayProductNotFund(barCode);
        }

    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }

    public void onTotal() {
        boolean productScanned = !productPrices.isEmpty();
        if (productScanned) {
            int totalValue = productPrices.stream().reduce(0, (x, y) -> x + y);
            display.displayTotal(totalValue);
        } else {
            display.noItemsSelected();
        }
    }
}
