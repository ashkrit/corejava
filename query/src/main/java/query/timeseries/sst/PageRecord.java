package query.timeseries.sst;

import model.avro.SSTablePage;

import java.util.NavigableMap;

public interface PageRecord<V> {
    NavigableMap<String, V> getPageData();

    SSTablePage getPageInfo();
}
