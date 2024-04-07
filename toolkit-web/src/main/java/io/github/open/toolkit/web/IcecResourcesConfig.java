package io.github.open.toolkit.web;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author jian.xu
 */
@Configuration
@EnableConfigurationProperties({IcecResourcesBean.class})
public class IcecResourcesConfig {

    @Bean
    public Map<String, String> icecResources(IcecResourcesBean icecResourcesBean) {
        return icecResourcesBean.getResources();
    }
}
