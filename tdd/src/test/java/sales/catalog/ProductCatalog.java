package sales.catalog;

import sales.Price;

public interface ProductCatalog {
    Price findPriceAsCents(String barCode);
}
