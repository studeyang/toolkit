package io.github.open.toolkit.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

class OnMissingLocationConfigFileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        MultiValueMap<String, Object> args = metadata.getAllAnnotationAttributes(ConditionalOnMissingLocationConfigFile.class.getName());
        String value = args.getFirst("value").toString();
        if (!StringUtils.hasText(value)) {
            return true;
        }

        for (PropertySource<?> propertySource : ((ConfigurableEnvironment)context.getEnvironment()).getPropertySources()) {
            if (propertySource.getName().contains(value)) {
                return false;
            }
        }

        return true;
    }

}
