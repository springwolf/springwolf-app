package io.github.springwolf.springwolfapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class SpringwolfKafkaProducer implements SpringwolfProducer {

    private static final Logger log = LoggerFactory.getLogger(SpringwolfKafkaProducer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AsyncDocFileService asyncDocFileService;

    @PostConstruct
    private void initialize() {
        String bootstrapServer = asyncDocFileService.getServers().get("kafka");
        if (bootstrapServer != null && !bootstrapServer.isBlank()) {
            log.info("Initializing kafka producer for bootstrap servers " + bootstrapServer);

            var factory = new DefaultKafkaProducerFactory<String, Object>(Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
            ));

            kafkaTemplate = new KafkaTemplate<>(factory);
        }
    }

    @Override
    public void send(String topic, Object payload) {
        if (kafkaTemplate == null) {
            log.warn("Can't publish to kafka - producer failed to initialize");
            throw new SpringwolfProducerException("Could not connect to Kafka");
        }

        log.info("Publishing to kafka topic {}: {}", topic, payload);
        kafkaTemplate.send(topic, toJson(payload));
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new SpringwolfProducerException("Can't convert payload to JSON string: " + payload);
        }
    }

}
