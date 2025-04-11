package io.github.toolkit.example;

import io.github.toolkit.config.annotation.PrepareConfigurations;
import io.github.toolkit.starter.annotation.EnableCache;
import io.github.toolkit.starter.annotation.EnableRequestLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@SpringBootApplication
@EnableRequestLog
@EnableCache
@PrepareConfigurations(group = "commons", value = {"test.yml"})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
