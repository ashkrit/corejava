package query.timeseries.sst;

import model.avro.SSTablePage;

import java.util.NavigableMap;

public class PageRecord<V> {
    public final NavigableMap<String, V> pageData;
    public final SSTablePage pageInfo;

    PageRecord(NavigableMap<String, V> pageData, SSTablePage pageInfo) {
        this.pageData = pageData;
        this.pageInfo = pageInfo;
    }

    public NavigableMap<String, V> getPageData() {
        return pageData;
    }

    public SSTablePage getPageInfo() {
        return pageInfo;
    }
}
