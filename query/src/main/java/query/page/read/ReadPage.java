package query.page.read;

import java.time.LocalDateTime;

public interface ReadPage {
    short version();

    int pageNumber();

    int totalRecords();

    LocalDateTime createdTime();

    PageIterator newIterator();

    int record(int index, byte[] buffer);

    static ReadPage create(byte[] buffer) {
        return new ReadableSlottedPage(buffer);
    }
}
