package io.github.springwolf.springwolfapp;

import java.util.Map;

public interface SpringwolfProducer {
    void send(String channelName, Map<String, Object> payload);
}
