package io.github.springwolf.springwolfapp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class SpringwolfAmpqProducer implements SpringwolfProducer {

    private static final Logger log = LoggerFactory.getLogger(SpringwolfAmpqProducer.class);
    private ConnectionFactory connectionFactory;

    @Autowired
    private AsyncDocFileService asyncDocFileService;

    @PostConstruct
    private void initialize() {
        String host = asyncDocFileService.getServers().get("ampq");

        if (host != null && !host.isBlank()) {
            log.info("Initializing AMPQ producer for host " + host);
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(host);
        }
    }

    @Override
    public void send(String queueName, Map<String, Object> payload) {
        if (connectionFactory == null) {
            log.warn("Can't publish to AMPQ - producer failed to initialize");
            throw new SpringwolfProducerException("Could not connect to AMPQ");
        }

        log.info("Publishing to AMPQ queue {}: {}", queueName, payload);

        try (Connection connection = connectionFactory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, payload.toString().getBytes());
        } catch (IOException | TimeoutException e) {
            var message = "Could not publish to AMPQ";
            log.warn(message, e);
            throw new SpringwolfProducerException(message);
        }

    }

}
