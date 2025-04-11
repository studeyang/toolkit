package io.github.toolkit.config.annotation;

import io.github.toolkit.config.ConfigurationLoader;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ConfigurationLoader.class)
public @interface PrepareConfigurations {

	String group() default "commons";
	String[] value() default {};
}
