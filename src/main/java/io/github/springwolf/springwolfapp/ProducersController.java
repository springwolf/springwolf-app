package io.github.springwolf.springwolfapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/asyncapi")
public class ProducersController {

    @Autowired
    private SpringwolfKafkaProducer kafkaProducer;

    @Autowired
    private SpringwolfAmqpProducer amqpProducer;

    @PostMapping("/kafka/publish")
    public void publishKafka(@RequestParam String topic, @RequestBody Map<String, Object> payload) {
        kafkaProducer.send(topic, payload);
    }

    @PostMapping("/amqp/publish")
    public void publishAmqp(@RequestParam String topic, @RequestBody Map<String, Object> payload) {
        amqpProducer.send(topic, payload);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SpringwolfProducerException.class)
    public ErrorMessage handle(SpringwolfProducerException exception) {
        return new ErrorMessage(exception.getMessage());
    }

}
