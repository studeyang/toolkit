package io.github.toolkit.example.configuration;

import io.github.toolkit.example.cache.UserCacheImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/8
 */
@Configuration
public class SystemConfiguration {

    @Bean
    public UserCacheImpl sendDictionaryCacheService() {
        return new UserCacheImpl();
    }

}
