package pos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class MerchantStore {

    private Display display;
    private ProductCatalog productCatalog;
    private Optional<Integer> priceInCents = Optional.empty();
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

        priceInCents = productCatalog.productPriceCents(barCode);
        if (priceInCents.isPresent()) {
            display.displayProductPrice(formatPrice(priceInCents.get()));
            productPrices.add(priceInCents.get());
        } else {
            display.displayProductNotFund(barCode);
        }

    }

    private String formatPrice(Integer priceAsText) {
        float priceIn$ = priceAsText / 100f;
        return String.format("$%.2f", priceIn$);
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }

    public void onTotal() {
        if (priceInCents.isPresent()) {
            int totalValue = productPrices.stream().reduce(0, (x, y) -> x + y);
            display.displayTotal(formatPrice(totalValue));
        } else {
            display.noItemsSelected();
        }
    }
}
