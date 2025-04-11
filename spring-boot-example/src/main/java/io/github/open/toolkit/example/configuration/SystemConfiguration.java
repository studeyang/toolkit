package io.github.open.toolkit.example.configuration;

import io.github.open.toolkit.example.cache.SendDictionaryCacheServiceImpl;
import io.github.toolkit.cache.dto.GuavaCacheSubscribeDto;
import io.github.toolkit.cache.pubsub.IGuavaCachePublisher;
import io.github.toolkit.cache.pubsub.ISubscribeListener;
import io.github.toolkit.cache.pubsub.redis.RedisClusterCacheListener;
import io.github.toolkit.cache.pubsub.redis.RedisClusterCachePublisher;
import io.github.toolkit.cache.servlet.CacheManagerServlet;
import io.github.toolkit.cache.util.SpringContextUtil;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/8
 */
@Configuration
public class SystemConfiguration {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public ServletRegistrationBean<CacheManagerServlet> cacheManager() {
        return new ServletRegistrationBean<>(new CacheManagerServlet(), "/cache/*");
    }

    @Bean
    public SendDictionaryCacheServiceImpl sendDictionaryCacheService() {
        return new SendDictionaryCacheServiceImpl();
    }

    @Bean
    public IGuavaCachePublisher redisTemplateCachePublisher(RedisTemplate redisTemplate) {
        return new RedisClusterCachePublisher(redisTemplate, "test-channel");
    }

    @Bean
    public ISubscribeListener<GuavaCacheSubscribeDto> guavaCacheSubscribeListener(RedisTemplate redisTemplate) {
        return new RedisClusterCacheListener(redisTemplate, "test-channel");
    }

}
