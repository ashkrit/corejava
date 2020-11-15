package query.sql;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class RecordFilterInfo {

    public final Predicate<Object> predicate;
    public final Set<IndexParameter> indexes;

    public RecordFilterInfo(Predicate<Object> predicate, Set<IndexParameter> indexes) {
        this.predicate = predicate;
        this.indexes = indexes;
    }

    static class IndexParameter {
        public final String indexName;
        public final String indexValue;

        IndexParameter(String indexName, String indexValue) {
            this.indexName = indexName;
            this.indexValue = indexValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IndexParameter that = (IndexParameter) o;
            return indexName.equals(that.indexName) &&
                    indexValue.equals(that.indexValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(indexName, indexValue);
        }

        @Override
        public String toString() {
            return "IndexParameter{" +
                    "indexName='" + indexName + '\'' +
                    ", indexValue='" + indexValue + '\'' +
                    '}';
        }
    }
}
