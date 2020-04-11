package tdd.kafa;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;

import static tdd.kafa.KafkaConnectionProperties.KAFKA_BROKER;

public class ConsumerApplication {

    public static void main(String[] args) {
        String topic = args[0];
        Consumer<Long, String> consumer = ConsumerCreator.createConsumer(KAFKA_BROKER,
                topic, "group1");
        int noMessageFound = 0;

        while (true) {
            ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofSeconds(5));
            if (consumerRecords.count() == 0) {
                noMessageFound++;
                if (noMessageFound > 1000) {
                    System.out.println("Too many miss message");
                    break;
                }
            }
            consumerRecords.forEach(record -> {
                System.out.println(String.format(
                        "Record  %s / %s meta [%s/%s]",
                        record.key(), record.value(), record.partition(), record.offset()));

            });
            consumer.commitAsync();
        }
        consumer.close();
    }
}
