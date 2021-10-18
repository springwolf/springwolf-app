package io.github.springwolf.springwolfapp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class SpringwolfKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(SpringwolfKafkaProducer.class);
    private Producer<String, String> producer;

    @Autowired
    private AsyncDocFileService asyncDocFileService;

    @PostConstruct
    private void initialize() {
        String bootstrapServer = asyncDocFileService.getServers().get("kafka");
        if (bootstrapServer != null && !bootstrapServer.isBlank()) {
            log.info("Initializing kafka producer");

            try {
                producer = buildProducer(bootstrapServer);
            } catch (Exception e) {
                log.error("Failed to initialize kafka producer", e);
            }
        }
    }

    private Producer<String, String> buildProducer(String bootstrapServers) {
        Map<String, Object> config = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.CLIENT_ID_CONFIG, "springwolf-app",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
        );

        return new KafkaProducer<>(config);
    }

    public void send(String topic, Map<String, Object> payload) {
        if (producer == null) {
            log.warn("Can't publish to kafka - producer failed to initialize");
            throw new SpringwolfProducerException("Could not connect to Kafka");
        }

        log.info("Publishing to kafka topic {}: {}", topic, payload);
        var record = new ProducerRecord<String, String>(topic, payload.toString());
        producer.send(record);
    }

}
