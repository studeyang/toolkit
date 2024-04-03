package io.github.open.toolkit.config.annotation;

import io.github.open.toolkit.config.ConfigurationLoader;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ConfigurationLoader.class)
public @interface PrepareConfigurations {

	String[] value() default {};
}
