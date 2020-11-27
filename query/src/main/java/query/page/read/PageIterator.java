package query.page.read;

public interface PageIterator {
    int next(byte[] buffer);

    boolean hasNext();
}
