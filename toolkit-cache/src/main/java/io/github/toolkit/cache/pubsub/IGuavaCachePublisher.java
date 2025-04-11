package io.github.toolkit.cache.pubsub;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;

public interface IGuavaCachePublisher {
    void publish(GuavaCacheSubscribeDto dto);

    void publish(String channel, Object value);

    void publish(String channel);

    String getChannel();

}
