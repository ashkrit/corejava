package encoding;


import encoding.buffer.PersistentBuffer;
import encoding.builder.binary.AvroTradeRecordBuilder;
import encoding.builder.binary.ChronicleTradeRecordBuilder;
import encoding.builder.binary.SBETradeRecordBuilder;
import encoding.builder.text.CSVTradeRecordBuilder;
import encoding.builder.text.JsonTradeRecordBuilder;
import encoding.record.RecordContainer;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class BufferApp {

    static int MB = 1024 * 1024;

    public static void main(String[] args) throws Exception {

        String location = args[0];
        Object[][] data = new Object[][]{
                {1L, new Random().nextLong(), "NYSE", "Buy", "GOOGL", 100},
                {2L, new Random().nextLong(), "NYSE", "Sell", "AAPL", 100},
        };

        int segmentSize = 64 * MB;

        process(new PersistentBuffer<>("Avro", toFile(location, "trade_avro.segment.0"), segmentSize, AvroTradeRecordBuilder::toBytes, AvroTradeRecordBuilder::fromBytes),
                AvroTradeRecordBuilder::newTrade, Arrays.stream(data));

        process(new PersistentBuffer<>("chronicle", toFile(location, "trade_chronicle.segment.0"), segmentSize, ChronicleTradeRecordBuilder::toBytes, ChronicleTradeRecordBuilder::fromBytes),
                ChronicleTradeRecordBuilder::newTrade, Arrays.stream(data));

        process(new PersistentBuffer<>("csv", toFile(location, "trade_csv.segment.0"), segmentSize, CSVTradeRecordBuilder::toBytes, CSVTradeRecordBuilder::fromBytes),
                CSVTradeRecordBuilder::newTrade, Arrays.stream(data));

        process(new PersistentBuffer<>("Sbe", toFile(location, "trade_sbe.segment.0"), segmentSize, SBETradeRecordBuilder::toBytes, SBETradeRecordBuilder::fromBytes),
                SBETradeRecordBuilder::newTrade, Arrays.stream(data));

        process(new PersistentBuffer<>("json", toFile(location, "trade_json.segment.0"), segmentSize, JsonTradeRecordBuilder::toBytes, JsonTradeRecordBuilder::fromBytes),
                JsonTradeRecordBuilder::newTrade, Arrays.stream(data));

        /*
        PersistentBuffer<Trade> buffer = new PersistentBuffer<>(segmentSize, toFile(location, "trade_avro.segment.0"),
                AvroTradeRecordBuilder::toBytes, AvroTradeRecordBuilder::fromBytes);

        buffer.read(291, ((offset, size, messageId, message) -> {
            System.out.println(messageId + ":" + message);
            return true;
        }));
         */

    }

    private static Path toFile(String location, String s) {
        return new File(String.format("%s/" + s, location)).toPath();
    }


    static <T> void process(RecordContainer<T> buffer, Function<Object[], T> recordBuilder, Stream<Object[]> data) {
        data.map(recordBuilder::apply).forEach(buffer::write);
        buffer.read((offset, size, id, b) -> {
            System.out.println(String.format("(%s) Pos:%s Size %s Bytes -> %s (%s)", buffer.formatName(), offset, size, id, b));
            return true;
        });
    }

}
