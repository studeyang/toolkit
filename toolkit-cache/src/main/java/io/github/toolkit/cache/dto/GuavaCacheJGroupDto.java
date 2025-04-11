package io.github.toolkit.cache.dto;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

public class GuavaCacheJGroupDto implements Serializable {
    private static final long serialVersionUID = 7146613372230394212L;
    public String cacheName;
    public String cacheKey;
    public String refreshCode;

    public GuavaCacheJGroupDto() {
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheKey() {
        return this.cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getRefreshCode() {
        return this.refreshCode;
    }

    public void setRefreshCode(String refreshCode) {
        this.refreshCode = refreshCode;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
