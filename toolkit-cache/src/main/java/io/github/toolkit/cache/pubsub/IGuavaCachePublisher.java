package io.github.toolkit.cache.pubsub;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;

public interface IGuavaCachePublisher {
    void publish(GuavaCacheSubscribeDto dto);

    void publish(String cacheName, Object cacheKey);

    void publish(String message);

    String getChannel();

}
