package io.github.toolkit.cache.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLoadingCache<K, V> implements ILocalCache<K, V> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractLoadingCache.class);
    private int maximumSize = 1000;
    private int expireAfterWriteDuration = 60;
    private TimeUnit timeUnit;
    private Date resetTime;
    private long highestSize;
    private Date highestTime;
    private LoadingCache<K, V> cache;
    private int maxRefreshCodeSize;
    private Map<String, String> refreshCodeMap;

    protected AbstractLoadingCache() {
        this.timeUnit = TimeUnit.MINUTES;
        this.highestSize = 0L;
        this.maxRefreshCodeSize = 30;
        this.refreshCodeMap = Collections.synchronizedMap(new LinkedHashMap<String, String>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return this.size() > maxRefreshCodeSize;
            }
        });
    }

    public LoadingCache<K, V> getCache() {
        if (this.cache == null) {
            synchronized (this) {
                if (this.cache == null) {
                    this.cache = CacheBuilder.newBuilder()
                            .maximumSize(this.maximumSize)
                            .expireAfterWrite(this.expireAfterWriteDuration, this.timeUnit)
                            .recordStats()
                            .build(new CacheLoader<K, V>() {
                                @Override
                                public V load(K key) {
                                    logger.info("AbstractLoadingCache loadData key = {}", key);
                                    return loadData(key);
                                }
                            });
                    this.resetTime = new Date();
                    this.highestTime = new Date();
                    logger.info("本地缓存{}初始化成功!", this.getClass().getSimpleName());
                }
            }
        }
        return this.cache;
    }

    protected abstract V loadData(K key);

    protected V getValue(K key) throws Exception {
        V result = this.getCache().get(key);
        if (this.getCache().size() > this.highestSize) {
            this.highestSize = this.getCache().size();
            this.highestTime = new Date();
        }

        return result;
    }

    @Override
    public void refresh(K key) {
        this.getCache().refresh(key);
    }

    @Override
    public void put(K key, V value) {
        this.getCache().put(key, value);
    }

    public long getHighestSize() {
        return this.highestSize;
    }

    public Date getHighestTime() {
        return this.highestTime;
    }

    public Date getResetTime() {
        return this.resetTime;
    }

    public void setResetTime(Date resetTime) {
        this.resetTime = resetTime;
    }

    public int getMaximumSize() {
        return this.maximumSize;
    }

    public int getExpireAfterWriteDuration() {
        return this.expireAfterWriteDuration;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void setExpireAfterWriteDuration(int expireAfterWriteDuration) {
        this.expireAfterWriteDuration = expireAfterWriteDuration;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public Map<String, String> getRefreshCodeMap() {
        return this.refreshCodeMap;
    }

    public void setRefreshCodeMap(Map<String, String> refreshCodeMap) {
        this.refreshCodeMap = refreshCodeMap;
    }

    public int getMaxRefreshCodeSize() {
        return this.maxRefreshCodeSize;
    }

    public void setMaxRefreshCodeSize(int maxRefreshCodeSize) {
        this.maxRefreshCodeSize = maxRefreshCodeSize;
    }
}
