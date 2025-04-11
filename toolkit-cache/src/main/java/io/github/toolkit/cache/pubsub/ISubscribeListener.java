package io.github.toolkit.cache.pubsub;

public interface ISubscribeListener<T> {
    void onMessage(T message);

    String getChannel();

}
