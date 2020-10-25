package db;

/*

    Key is stored in SortedMap for efficient range scan
    Key format : {tableName}/{indexName}/{indexValue}/{rowId}

    PK :
        orders/pk/1 -> Row
        orders/pk/2 -> Row
        orders/pk/3 -> Row

   Secondary: By Status

        orders/status/SHIPPED/1 -> 1
        orders/status/SHIPPED/2 -> 2
        orders/status/SHIPPED/3 -> 2

   Secondary: By Status+date
        orders/status/SHIPPED#20200901/1 -> 1
        orders/status/SHIPPED#20200901/2 -> 2
        orders/status/SHIPPED#20201012/3 -> 2

   Secondary: Customer+Order
        orders/co/100#1/1 -> 1
        orders/co/100#2/2 -> 2
        orders/co/101#3/3 -> 3

 */
public class KeyBuilder {
    final String tableName;

    public KeyBuilder(String tableName) {
        this.tableName = tableName;
    }

    public String secondaryIndexKey(String indexName, String indexValue, String rowKey) {
        return String.format("%s/%s/%s/%s", tableName, indexName, indexValue, rowKey);
    }

    public String searchKey(String indexName, String indexValue) {
        return String.format("%s/%s/%s", tableName, indexName, indexValue);
    }

    public String primaryKey() {
        return String.format("%s/%s", tableName, "pk");
    }
}
