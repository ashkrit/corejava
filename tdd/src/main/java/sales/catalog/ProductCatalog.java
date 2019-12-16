package sales.catalog;

import sales.Price;

public interface ProductCatalog {
    Price findPrice(String barCode);
}
