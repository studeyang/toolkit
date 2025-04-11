package io.github.toolkit.cache.pubsub.redis;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;
import io.github.toolkit.cache.pubsub.IGuavaCachePublisher;
import io.github.toolkit.cache.util.FastJSONHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class JedisCachePublisher implements IGuavaCachePublisher {
    private static final Logger logger = LoggerFactory.getLogger(JedisCachePublisher.class);
    private JedisSentinelPool jedisSentinelPool;
    private ThreadPoolExecutor executors;
    private String channel;

    public JedisCachePublisher(JedisSentinelPool jedisSentinelPool, String channel) {
        this.jedisSentinelPool = jedisSentinelPool;
        this.channel = channel;
        this.executors = this.getPublisherThreads();
    }

    @Override
    public void publish(String cacheName, Object cacheKey) {
        GuavaCacheSubscribeDto dto = new GuavaCacheSubscribeDto();
        dto.setCacheName(cacheName);
        if (cacheKey != null && !"".equals(cacheKey)) {
            dto.setCacheKey(FastJSONHelper.serialize(cacheKey));
        }

        dto.setRefreshCode(UUID.randomUUID().toString());
        this.publish(dto);
    }

    @Override
    public void publish(String message) {
        GuavaCacheSubscribeDto dto = FastJSONHelper.deserialize(message, GuavaCacheSubscribeDto.class);
        dto.setRefreshCode(UUID.randomUUID().toString());
        this.publish(dto);
    }

    @Override
    public void publish(final GuavaCacheSubscribeDto dto) {
        this.jedisPublish(dto);
        this.executors.execute(() -> {
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException ignored) {
            }

            JedisCachePublisher.this.jedisPublish(dto);
        });
    }

    private void jedisPublish(GuavaCacheSubscribeDto dto) {
        Jedis jedis = null;

        try {
            String message = FastJSONHelper.serialize(dto);
            jedis = this.jedisSentinelPool.getResource();
            jedis.publish(this.channel, message);
        } catch (Exception var7) {
            logger.error("GuavaCachePublisher jedisPublish error ", var7);
        }

    }

    private ThreadPoolExecutor getPublisherThreads() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    @Override
    public String getChannel() {
        return this.channel;
    }
}
