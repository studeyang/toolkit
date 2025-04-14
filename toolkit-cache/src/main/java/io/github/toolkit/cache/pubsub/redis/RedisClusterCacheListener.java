package io.github.toolkit.cache.pubsub.redis;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;
import io.github.toolkit.cache.guava.GuavaCacheManager;
import io.github.toolkit.cache.pubsub.ISubscribeListener;
import io.github.toolkit.cache.util.FastJSONHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class RedisClusterCacheListener implements ISubscribeListener<GuavaCacheSubscribeDto>, InitializingBean, SmartLifecycle {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RedisMessageListenerContainer listenerContainer;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final String channel;

    public RedisClusterCacheListener(RedisTemplate<Object, Object> redisTemplate, String channel) {
        this.redisTemplate = redisTemplate;
        this.channel = channel;
    }

    @Override
    public void afterPropertiesSet() {
        ChannelTopic topic = new ChannelTopic(channel);
        MessageListener messageListener = (message, pattern) -> {
            String body = (String) redisTemplate.getDefaultSerializer().deserialize(message.getBody());
            GuavaCacheSubscribeDto dto = FastJSONHelper.deserialize(body, GuavaCacheSubscribeDto.class);
            this.onMessage(dto);
        };
        listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(redisTemplate.getConnectionFactory());
        listenerContainer.addMessageListener(messageListener, topic);
        listenerContainer.afterPropertiesSet();
    }

    @Override
    public void onMessage(GuavaCacheSubscribeDto message) {
        logger.info("GuavaCacheSubscribeListener onMessage start, message = {}", message);

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
        } catch (Exception e) {
            logger.error("GuavaCacheSubscribeListener onMessage FastJSONHelper error , message is {} ", message, e);
        } finally {
            logger.info("GuavaCacheSubscribeListener onMessage end, message = {}", message);
        }
    }

    @Override
    public String getChannel() {
        return this.channel;
    }

    private boolean isEmptyOrEmptyString(String content) {
        return StringUtils.isEmpty(content) || "null".equals(content.trim()) || "\"\"".equals(content);
    }

    @Override
    public boolean isAutoStartup() {
        return listenerContainer.isAutoStartup();
    }

    @Override
    public void stop(Runnable callback) {
        listenerContainer.stop(callback);
    }

    @Override
    public void start() {
        listenerContainer.start();
    }

    @Override
    public void stop() {
        listenerContainer.stop();
    }

    @Override
    public boolean isRunning() {
        return listenerContainer.isRunning();
    }

    @Override
    public int getPhase() {
        return listenerContainer.getPhase();
    }
}