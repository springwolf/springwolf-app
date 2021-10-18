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

    @PostMapping("/kafka/publish")
    public void publish(@RequestParam String topic, @RequestBody Map<String, Object> payload) {
        kafkaProducer.send(topic, payload);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SpringwolfProducerException.class)
    public ErrorMessage handle(SpringwolfProducerException exception) {
        return new ErrorMessage(exception.getMessage());
    }

}
