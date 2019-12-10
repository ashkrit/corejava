package pos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class MerchantStore {

    private final Display display;
    private final ProductCatalog productCatalog;
    private final Collection<Integer> basketItems = new ArrayList<>();

    public MerchantStore(Display display, ProductCatalog productCatalog) {
        this.display = display;
        this.productCatalog = productCatalog;
    }

    public void onBarCode(String barCode) {
        if (isNullOrEmpty(barCode)) {
            display.displayInvalidScan();
            return;
        }

        Optional<Integer> priceInCents = productCatalog.priceAsCents(barCode);

        priceInCents.ifPresent(this::addToBasketAndShowProduct);

        if (!priceInCents.isPresent()) {
            display.displayProductNotFund(barCode);
        }

    }

    private void addToBasketAndShowProduct(Integer price) {
        basketItems.add(price);
        display.displayProductPrice(price);
    }

    private boolean isNullOrEmpty(String barCode) {
        return barCode == null || barCode.trim().length() == 0;
    }

    public void onTotal() {
        if (basketItems.isEmpty()) {
            display.noItemsSelected();
        } else {
            display.displayTotal(calculateTotal(basketItems));
        }
    }

    private int calculateTotal(Collection<Integer> productPrices) {
        return productPrices.stream().reduce(0, Integer::sum);
    }
}
