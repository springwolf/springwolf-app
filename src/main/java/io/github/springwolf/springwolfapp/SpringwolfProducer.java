package io.github.springwolf.springwolfapp;

public interface SpringwolfProducer {
    void send(String channelName, Object payload);
}
