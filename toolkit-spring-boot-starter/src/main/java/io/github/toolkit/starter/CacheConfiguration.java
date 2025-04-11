package io.github.toolkit.starter;

import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;
import io.github.toolkit.cache.pubsub.IGuavaCachePublisher;
import io.github.toolkit.cache.pubsub.ISubscribeListener;
import io.github.toolkit.cache.pubsub.redis.RedisClusterCacheListener;
import io.github.toolkit.cache.pubsub.redis.RedisClusterCachePublisher;
import io.github.toolkit.cache.servlet.CacheManagerServlet;
import io.github.toolkit.cache.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/11
 */
@Configuration
@ConditionalOnClass(CacheManagerServlet.class)
public class CacheConfiguration {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public ServletRegistrationBean<CacheManagerServlet> cacheManager() {
        return new ServletRegistrationBean<>(new CacheManagerServlet(), "/cache/*");
    }

    @Bean
    @ConditionalOnMissingBean(IGuavaCachePublisher.class)
    public IGuavaCachePublisher guavaCachePublisher(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisClusterCachePublisher(redisTemplate, "default-cache-channel");
    }

    @Bean
    @ConditionalOnMissingBean(ISubscribeListener.class)
    public ISubscribeListener<GuavaCacheSubscribeDto> guavaCacheListener(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisClusterCacheListener(redisTemplate, "default-cache-channel");
    }

}
