package query.page.write;

import java.time.LocalDateTime;

public interface WritePage {

    short version();

    int pageNumber();

    int noOfTuple();

    LocalDateTime createdTime();

    int write(byte[] bytes);

    byte[] commit();

    int capacity();
}
