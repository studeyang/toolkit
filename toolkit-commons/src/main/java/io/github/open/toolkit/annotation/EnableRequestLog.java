package io.github.open.toolkit.annotation;

import io.github.open.toolkit.config.RequestLogAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import(RequestLogAutoConfiguration.class)
public @interface EnableRequestLog {
}
