package query.timeseries.sst.memory;

import model.avro.page.SSTablePage;
import query.timeseries.sst.PageRecord;

import java.util.NavigableMap;

public class InMemoryPageRecord<V> implements PageRecord<V> {

    public final NavigableMap<String, V> pageData;
    public final SSTablePage pageInfo;

    InMemoryPageRecord(NavigableMap<String, V> pageData, SSTablePage pageInfo) {
        this.pageData = pageData;
        this.pageInfo = pageInfo;
    }

    @Override
    public NavigableMap<String, V> getPageData() {
        return pageData;
    }

    @Override
    public SSTablePage getPageInfo() {
        return pageInfo;
    }


}
