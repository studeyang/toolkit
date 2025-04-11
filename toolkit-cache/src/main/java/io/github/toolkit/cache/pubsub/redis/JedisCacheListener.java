package io.github.toolkit.cache.pubsub.redis;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;
import io.github.toolkit.cache.guava.GuavaCacheManager;
import io.github.toolkit.cache.pubsub.AbstractSubscribeListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisSentinelPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class JedisCacheListener extends AbstractSubscribeListener<GuavaCacheSubscribeDto> {

    private static final Logger logger = LoggerFactory.getLogger(JedisCacheListener.class);

    public JedisCacheListener(JedisSentinelPool jedisSentinelPool, String channel) {
        super(jedisSentinelPool, channel);
    }

    @Override
    public void onMessage(GuavaCacheSubscribeDto message) {
        logger.info("GuavaCacheSubscribeListener onMessage start, message =  {}", message);

        try {
            if (message == null) {
                logger.warn("GuavaCacheSubscribeListener onMessage error, because of message is null");
                return;
            }

            if (this.isEmptyOrEmptyString(message.getCacheName())) {
                logger.warn("GuavaCacheSubscribeListener onMessage error, because of message.getCacheName is null");
                return;
            }

            if (this.isEmptyOrEmptyString(message.getCacheKey())) {
                GuavaCacheManager.resetCache(message.getCacheName(), message.getRefreshCode());
                return;
            }

            if (this.isEmptyOrEmptyString(message.getCacheKey())) {
                return;
            }

            GuavaCacheManager.refresh(message.getCacheName(), message.getCacheKey(), message.getRefreshCode());
        } catch (Exception var6) {
            logger.error("GuavaCacheSubscribeListener onMessage FastJSONHelper error, message is {} ", message, var6);
        } finally {
            logger.info("GuavaCacheSubscribeListener onMessage end, message = {}", message);
        }

    }

    private boolean isEmptyOrEmptyString(String content) {
        return StringUtils.isEmpty(content) || "null".equals(content.trim()) || "\"\"".equals(content);
    }

    @Override
    protected ThreadPoolExecutor getSubscriberThreads() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

}
