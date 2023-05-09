package io.github.open.toolkit.config;

import io.github.open.toolkit.aop.RequestLogAspect;
import io.github.open.toolkit.filter.HttpServletRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Configuration
@Slf4j
@EnableAspectJAutoProxy
public class RequestLogAutoConfiguration {

    @Bean
    public HttpServletRequestFilter httpServletRequestFilter() {
        return new HttpServletRequestFilter();
    }

    @Bean
    public RequestLogAspect requestLogAspect() {
        return new RequestLogAspect();
    }

    @Bean
    public ResponseLogAdvice responseLogAdvice() {
        return new ResponseLogAdvice();
    }

}
