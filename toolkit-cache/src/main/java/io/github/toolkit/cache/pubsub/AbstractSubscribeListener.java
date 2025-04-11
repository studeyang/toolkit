package io.github.toolkit.cache.pubsub;

import io.github.toolkit.cache.exception.CacheException;
import io.github.toolkit.cache.util.FastJSONHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractSubscribeListener<T> extends JedisPubSub implements ISubscribeListener<T>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSubscribeListener.class);
    protected ThreadPoolExecutor executors;

    private final JedisSentinelPool jedisSentinelPool;
    private final String channel;

    protected AbstractSubscribeListener(JedisSentinelPool jedisSentinelPool, String channel) {
        this.jedisSentinelPool = jedisSentinelPool;
        this.channel = channel;
    }

    protected abstract ThreadPoolExecutor getSubscriberThreads();

    @Override
    public String getChannel() {
        return this.channel;
    }
    @Override
    public void onMessage(String channel, String message) {
        logger.info(" received onMessage message from channel 【{}】: {}", channel, message);

        try {
            Type type = this.getClass().getGenericSuperclass();
            Type[] trueType = ((ParameterizedType) type).getActualTypeArguments();
            Class<T> entityClass = (Class) trueType[0];
            final T t = FastJSONHelper.deserialize(message, entityClass);
            this.executors.execute(() -> this.onMessage(t));
        } catch (Exception e) {
            logger.error("AbstractSubscribeListener onMessage error ", e);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.info("AbstractSubscribeListener onSubscribe channel: {}, channels count: {}", channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.info("AbstractSubscribeListener onUnsubscribe channel: {} channels count:{} ", channel, subscribedChannels);
    }

    @Override
    public void onPMessage(String paramString1, String paramString2, String paramString3) {
        throw CacheException.UNKNOWN_SERVICE;
    }

    @Override
    public void onPSubscribe(String paramString, int paramInt) {
        throw CacheException.UNKNOWN_SERVICE;
    }

    @Override
    public void onPUnsubscribe(String paramString, int paramInt) {
        throw CacheException.UNKNOWN_SERVICE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("AbstractSubscribeListener Listener {} start*******", this.getClass().getName());
        if (StringUtils.isEmpty(this.getChannel())) {
            throw new IllegalAccessException("AbstractSubscribeListener channel name is empty");
        } else if (this.jedisSentinelPool == null) {
            throw new IllegalAccessException("AbstractSubscribeListener jedisSentinelPool is empty");
        } else {
            this.executors = this.getSubscriberThreads();
            this.startSubscribe();
        }
    }

    private void startSubscribe() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Jedis jedis = this.jedisSentinelPool.getResource();
            jedis.subscribe(this, this.getChannel());
        });
    }

}
