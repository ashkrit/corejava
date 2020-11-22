package query.page;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlottedPageTest {


    @Test
    public void writeHeader() {
        SlotPage page = new SlotPage(1024);

        page.version((short) 1);
        page.pageNumber(2);
        page.noOfTuple(20);

        byte[] data = page.commit();

        ByteBuffer buffer = ByteBuffer.wrap(data);

        assertEquals(1, buffer.getShort(PageOffSets.PAGE_VERSION));
        assertEquals(2, buffer.getInt(PageOffSets.PAGE_NUMBER));
        assertEquals(20, buffer.getInt(PageOffSets.NO_OF_TUPLE));

    }

    @Test
    public void read_headers() {

        SlotPage expected = new SlotPage(1024);
        expected.version((short) 1);
        expected.pageNumber(2);
        expected.noOfTuple(20);

        byte[] data = expected.commit();

        SlotPage actual = new SlotPage(data);

        assertEquals(actual.version(), expected.version());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(expected.noOfTuple(), expected.noOfTuple());

    }


}
