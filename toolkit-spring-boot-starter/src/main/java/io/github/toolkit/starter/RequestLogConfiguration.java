package io.github.toolkit.starter;

import io.github.toolkit.commons.aop.RequestLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Configuration
@EnableAspectJAutoProxy
public class RequestLogConfiguration {

    @Bean
    public RequestLogAspect requestLogAspect() {
        return new RequestLogAspect();
    }

}
