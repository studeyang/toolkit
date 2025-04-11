package io.github.toolkit.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Properties;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0.0 2024/4/3
 */
public class MultiProfilesYamlPropertiesFactoryBean extends YamlPropertiesFactoryBean {

    private final ConfigurableEnvironment environment;

    public MultiProfilesYamlPropertiesFactoryBean(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    protected Properties createProperties() {
        final Properties result = createStringAdaptingProperties();
        process((properties, map) -> {
            if (result.isEmpty()) {
                result.putAll(properties);
            } else {
                for (String profile : environment.getActiveProfiles()) {
                    if (profile.equals(properties.get("spring.profiles"))) {
                        result.putAll(properties);
                    }
                }
            }
        });
        return result;
    }

    public static Properties createStringAdaptingProperties() {
        return new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
    }

}
