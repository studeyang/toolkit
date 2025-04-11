package io.github.toolkit.starter.annotation;

import io.github.toolkit.starter.CacheConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/11
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import(CacheConfiguration.class)
public @interface EnableCache {
}
