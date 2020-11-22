package query.page;

public class PageOffSets {

    private static int SIZEOF_BYTE = 1;
    private static int SIZEOF_INT = 4;
    private static int SIZEOF_LONG = 8;

    public static final int PAGE_VERSION = 0;
    public static final int PAGE_NUMBER = PAGE_VERSION + SIZEOF_BYTE;
    public static final int NO_OF_TUPLE = PAGE_NUMBER + SIZEOF_INT;
    public static final int CREATED_TS = NO_OF_TUPLE + SIZEOF_INT;


    public static final int DATA_OFFSET = CREATED_TS + SIZEOF_LONG;


}
