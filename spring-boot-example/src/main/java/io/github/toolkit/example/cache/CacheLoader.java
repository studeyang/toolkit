package io.github.toolkit.example.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/10
 */
@Component
public class CacheLoader implements ApplicationRunner {

    @Autowired
    private UserCacheImpl userCacheImpl;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("userCacheImpl: " + userCacheImpl.get("01"));
        System.out.println("userCacheImpl: " + userCacheImpl.get("02"));
    }
}
