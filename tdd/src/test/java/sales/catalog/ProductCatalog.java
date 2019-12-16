package sales.catalog;

import sales.Price;

public interface ProductCatalog {
    Float findPrice(String barCode);

    Price findPriceAsCents(String barCode);
}
