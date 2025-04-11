package io.github.toolkit.cache.dto;

import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/11
 */
@Data
public class GuavaCacheSubscribeDto implements Serializable {
    private static final long serialVersionUID = 7146613372230394212L;
    private String cacheName;
    private String cacheKey;
    private String refreshCode;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
