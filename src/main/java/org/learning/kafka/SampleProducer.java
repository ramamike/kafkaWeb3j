package org.learning.kafka;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

public class SampleProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "simpleTopic";
    private static final Integer partition = Integer.getInteger("1");
    private static int COUNT = 5;
    private static final String MESSAGE = "message";
    private final KafkaSender<Integer, String> sender;

    private DateTimeFormatter dateFormat;
    private static final Logger log = LoggerFactory.getLogger(SampleProducer.class.getName());

    public SampleProducer(String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<Integer, String> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);
        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS")
                .withZone(ZoneId.systemDefault());
    }

    public void sendMessages(String topic, String message, int count) {
        int i = 0;
        while (i < count) {
            i++;
            SenderRecord<Integer, String, Integer> outbound =
                    SenderRecord.create(new ProducerRecord<>(topic, i, message + "" + i), i);
            sender.send(Mono.just(outbound))
                    .doOnError(e -> log.error("Send failed", e))
//                    .subscribe();
                    .subscribe(r -> {
                        RecordMetadata metadata = r.recordMetadata();

                        Instant timestamp = Instant.ofEpochMilli(metadata.timestamp());
                        System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                                r.correlationMetadata(),
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset(),
                                dateFormat.format(timestamp));
                    });
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void close() {
        sender.close();
    }

    public static void main(String[] args) {
        SampleProducer producer = new SampleProducer(BOOTSTRAP_SERVERS);
        producer.sendMessages(TOPIC, MESSAGE, COUNT);
        producer.close();
    }

}