package io.github.toolkit.cache.pubsub.redis;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;
import io.github.toolkit.cache.pubsub.IGuavaCachePublisher;
import io.github.toolkit.cache.util.FastJSONHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class RedisClusterCachePublisher implements IGuavaCachePublisher {
    private final RedisTemplate redisTemplate;
    private final String channel;
    private ThreadPoolExecutor executors;


    public RedisClusterCachePublisher(RedisTemplate redisTemplate, String channel) {
        super();
        this.redisTemplate = redisTemplate;
        this.channel = channel;
        this.executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    @Override
    public void publish(String cacheName, Object cacheKey) {
        GuavaCacheSubscribeDto dto = new GuavaCacheSubscribeDto();
        dto.setCacheName(cacheName);
        dto.setCacheKey(FastJSONHelper.serialize(cacheKey));
        dto.setRefreshCode(UUID.randomUUID().toString());
        publish(dto);
    }

    @Override
    public void publish(String message) {
        GuavaCacheSubscribeDto dto = FastJSONHelper.deserialize(message, GuavaCacheSubscribeDto.class);
        publish(dto);
    }

    @Override
    public String getChannel() {
        return this.channel;
    }

    @Override
    public void publish(GuavaCacheSubscribeDto dto) {
        doPublish(dto);
        executors.execute(() -> {
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            doPublish(dto);
        });
    }

    private void doPublish(GuavaCacheSubscribeDto dto) {
        redisTemplate.convertAndSend(channel, FastJSONHelper.serialize(dto));
    }
}