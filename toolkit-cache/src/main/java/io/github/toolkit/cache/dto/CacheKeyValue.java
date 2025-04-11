package io.github.toolkit.cache.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheKeyValue<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private K cacheKey;
    private V cacheValue;

}