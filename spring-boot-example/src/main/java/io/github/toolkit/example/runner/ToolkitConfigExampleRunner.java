package io.github.toolkit.example.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/11
 */
@Component
public class ToolkitConfigExampleRunner implements ApplicationRunner {

    @Value("${test.config}")
    private String config;

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(config);
    }
}
