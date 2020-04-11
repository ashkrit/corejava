package tdd.kafa;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicLong;

import static tdd.kafa.KafkaConnectionProperties.KAFKA_BROKER;

public class ProducerApplication {

    public static void main(String[] args) {

        String topicName = args[0];
        Producer<Long, String> producer = ProducerCreator.createProducer(KAFKA_BROKER, "client1");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        AtomicLong messageId = new AtomicLong();
        reader.lines().forEach(line -> {
            try {
                ProducerRecord<Long, String> record = new ProducerRecord<>(topicName, messageId.incrementAndGet(),
                        line);

                RecordMetadata metadata = producer.send(record).get();
                System.out.println("Record sent with key " + line + " to partition " + metadata.partition()
                        + " with offset " + metadata.offset());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }
}
