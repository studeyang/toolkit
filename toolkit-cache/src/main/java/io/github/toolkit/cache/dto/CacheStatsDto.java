package io.github.toolkit.cache.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheStatsDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cacheName;
    private Long size;
    private Integer maximumSize;
    private Integer survivalDuration;
    private Long hitCount;
    private String hitRate;
    private String missRate;
    private Long loadSuccessCount;
    private Long loadExceptionCount;
    private Long totalLoadTime;
    private String resetTime;
    private Long highestSize;
    private String highestTime;
    private String survivalDurationUnit;

}
