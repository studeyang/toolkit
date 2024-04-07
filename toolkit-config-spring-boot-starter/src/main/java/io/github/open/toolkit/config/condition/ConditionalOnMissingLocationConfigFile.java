package io.github.open.toolkit.config.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Conditional(OnMissingLocationConfigFileCondition.class)
public @interface ConditionalOnMissingLocationConfigFile {
    String value() default "";
}
