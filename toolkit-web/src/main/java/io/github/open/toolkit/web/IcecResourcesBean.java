package io.github.open.toolkit.web;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author jian.xu
 */

@ConfigurationProperties(
        prefix = "icec"
)
public class IcecResourcesBean {
    private Map<String, String> resources;

    public IcecResourcesBean() {
    }

    public Map<String, String> getResources() {
        return this.resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }
}