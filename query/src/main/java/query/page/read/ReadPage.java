package query.page.read;

import java.time.LocalDateTime;

public interface ReadPage {
    short version();

    int pageNumber();

    int totalRecords();

    LocalDateTime createdTime();

    int read(byte[] readBuffer);

    boolean hasNext();


    static ReadPage create(byte[] buffer) {
        return new ReadableSlottedPage(buffer);
    }
}
