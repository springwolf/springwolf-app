package io.github.springwolf.springwolfapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class SpringwolfAmqpProducer implements SpringwolfProducer {

    private static final Logger log = LoggerFactory.getLogger(SpringwolfAmqpProducer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private ConnectionFactory connectionFactory;

    @Autowired
    private AsyncDocFileService asyncDocFileService;

    @PostConstruct
    private void initialize() {
        String host = asyncDocFileService.getServers().get("amqp");

        if (host != null && !host.isBlank()) {
            log.info("Initializing AMQP producer for host " + host);
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(host);
        }
    }

    @Override
    public void send(String queueName, Object payload) {
        if (connectionFactory == null) {
            log.warn("Can't publish to AMQP - producer failed to initialize");
            throw new SpringwolfProducerException("Could not connect to AMQP");
        }

        log.info("Publishing to AMQP queue {}: {}", queueName, payload);

        try (Connection connection = connectionFactory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);

            byte[] payloadAsJson = objectMapper.writeValueAsBytes(payload);
            channel.basicPublish("", queueName, null, payloadAsJson);
        } catch (IOException | TimeoutException e) {
            var message = "Could not publish to AMQP";
            log.warn(message, e);
            throw new SpringwolfProducerException(message);
        }
    }

}
