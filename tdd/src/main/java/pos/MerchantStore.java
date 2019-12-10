package pos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class MerchantStore {
    private boolean newAlgo = false;
    private Display display;
    private ProductCatalog productCatalog;
    private Optional<String> priceAsText = Optional.empty();
    private Optional<Integer> priceInCents = Optional.empty();
    private Collection<Integer> productPrices = new ArrayList<>();

    public MerchantStore(Display display, ProductCatalog productCatalog) {
        this.display = display;
        this.productCatalog = productCatalog;
    }

    public MerchantStore(Display display, ProductCatalog productCatalog, boolean newAlgo) {
        this.display = display;
        this.productCatalog = productCatalog;
        this.newAlgo = newAlgo;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            display.displayInvalidScan();
            return;
        }

        priceAsText = productCatalog.productPrice(barCode);
        priceInCents = productCatalog.productPriceCents(barCode);
        if (priceAsText.isPresent()) {
            display.displayProductPrice(formatPrice(priceInCents.get()));
            productPrices.add(priceInCents.get());
        } else {
            display.displayProductNotFund(barCode);
        }

    }

    private String formatPrice(String priceAsText) {
        return priceAsText;
    }

    private String formatPrice(Integer priceAsText) {
        float priceIn$ = priceAsText / 100f;
        return String.format("$%.2f", priceIn$);
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }

    public void onTotal() {
        if (priceAsText.isPresent()) {
            int totalValue = productPrices.stream().reduce(0, (x, y) -> x + y);
            display.displayTotal(formatPrice(totalValue));
        } else {
            display.noItemsSelected();
        }
    }
}
