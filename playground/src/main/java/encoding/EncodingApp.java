package encoding;


import encoding.builder.binary.AvroTradeRecordBuilder;
import encoding.builder.binary.ChronicleTradeRecordBuilder;
import encoding.builder.binary.SBETradeRecordBuilder;
import encoding.builder.text.CSVTradeRecordBuilder;
import encoding.builder.text.JsonTradeRecordBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class EncodingApp {

    public static void main(String[] args) {

        Object[][] data = new Object[][]{
                {new Random().nextLong(), new Random().nextLong(), "NYSE", "Buy", "GOOGL", 100},
                {new Random().nextLong(), new Random().nextLong(), "NYSE", "Sell", "AAPL", 100},
        };

        process("Avro", Arrays.stream(data), AvroTradeRecordBuilder::newTrade, AvroTradeRecordBuilder::toBytes, AvroTradeRecordBuilder::fromBytes);
        process("chronicle", Arrays.stream(data), ChronicleTradeRecordBuilder::newTrade, ChronicleTradeRecordBuilder::toBytes, ChronicleTradeRecordBuilder::fromBytes);
        process("sbe", Arrays.stream(data), SBETradeRecordBuilder::newTrade, SBETradeRecordBuilder::toBytes, SBETradeRecordBuilder::fromBytes);

        process("csv", Arrays.stream(data), CSVTradeRecordBuilder::newTrade, CSVTradeRecordBuilder::toBytes, CSVTradeRecordBuilder::fromBytes);
        process("json", Arrays.stream(data), JsonTradeRecordBuilder::newTrade, JsonTradeRecordBuilder::toBytes, JsonTradeRecordBuilder::fromBytes);

    }

    static <T> void process(String formatType, Stream<Object[]> data, Function<Object[], T> recordBuilder, Function<T, byte[]> toBytes, Function<byte[], T> fromBytes) {
        data.map(recordBuilder)
                .map(toBytes)
                .peek(x -> System.out.println(String.format("(%s) -> Size %s Bytes", formatType, x.length)))
                .map(fromBytes)
                .forEach(o -> System.out.println(o));

        System.out.println();
    }

}
