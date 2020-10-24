package db;

public class KeyBuilder {
    final String tableName;

    /*
      Format
      table/index/indexvalue/rowid
     */
    public KeyBuilder(String tableName) {
        this.tableName = tableName;
    }

    public String secondaryIndex(String indexName, String indexValue, String rowKey) {
        return String.format("%s/%s/%s/%s", tableName, indexName, indexValue, rowKey);
    }

    public String primaryIndex(String indexName, String indexValue) {
        return String.format("%s/%s/%s", tableName, indexName, indexValue);
    }

    public String primaryIndex() {
        return String.format("%s/%s", tableName, "pk");
    }
}
