package com.org;

import com.org.jdbcproxy.custom.CustomDataSourceContext;
import com.org.jdbcproxy.custom.CustomDataSourceContext.CollectionTable;
import com.org.jdbcproxy.custom.CustomDataSourceContext.CollectionTable.ColumnInfo;
import com.org.jdbcproxy.fs.EmbedDatabase;

import java.time.LocalDate;
import java.util.*;

public class CollectionSQL {

    public static void main(String[] args) {


        CustomDataSourceContext context = new CustomDataSourceContext(() -> EmbedDatabase.open("jdbc:sqlite::memory:"), new HashMap<>());


        Map<String, Purchase> purchases = new HashMap<>();

        purchases.put("c1", new Purchase("c1", LocalDate.now(), "m1", 100));
        purchases.put("c2", new Purchase("c2", LocalDate.now(), "m2", 50));
        purchases.put("c3", new Purchase("c3", LocalDate.now(), "m3", 200));

        CollectionTable<Purchase> collectionTable = createCollectionTable(purchases);
        context.collectionLoadFunctions.put("merchant", collectionTable);


    }

    private static CollectionTable<Purchase> createCollectionTable(Map<String, Purchase> purchases) {
        List<ColumnInfo> columns = Arrays.asList(
                new ColumnInfo("customer_id", "string"),
                new ColumnInfo("purchase_date", "integer"),
                new ColumnInfo("merchant", "string"),
                new ColumnInfo("purchase_amount", "real")
        );
        return new CollectionTable<>(
                columns, Collections.singletonList("customer_id"), (key, value) -> new Object[]{
                value.customerId,
                value.purchaseDate.toEpochDay(),
                value.merchant,
                value.purchaseAmount
        }, purchases);
    }


    public static class Purchase {
        public final String customerId;
        public final LocalDate purchaseDate;
        public final String merchant;
        public final double purchaseAmount;

        public Purchase(String customerId, LocalDate purchaseDate, String merchant, double purchaseAmount) {
            this.customerId = customerId;
            this.purchaseDate = purchaseDate;
            this.merchant = merchant;
            this.purchaseAmount = purchaseAmount;
        }
    }
}
