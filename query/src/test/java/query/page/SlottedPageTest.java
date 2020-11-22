package query.page;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlottedPageTest {


    @Test
    public void writeHeader() {
        SlotPage page = new SlotPage(1024);

        page.version((short) 1);
        page.pageNo(2);
        page.noOfTuple(20);

        byte[] data = page.commit();

        ByteBuffer buffer = ByteBuffer.wrap(data);

        assertEquals(1, buffer.getShort(PageOffSets.PAGE_VERSION));
        assertEquals(2, buffer.getInt(PageOffSets.PAGE_NUMBER));
        assertEquals(20, buffer.getInt(PageOffSets.NO_OF_TUPLE));

    }
}
