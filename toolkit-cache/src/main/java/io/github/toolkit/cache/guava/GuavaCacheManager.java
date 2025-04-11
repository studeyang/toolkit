package io.github.toolkit.cache.guava;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheStats;
import io.github.toolkit.cache.dto.CacheKeyValue;
import io.github.toolkit.cache.dto.CacheStatsDto;
import io.github.toolkit.cache.dto.PageInfo;
import io.github.toolkit.cache.util.SpringContextUtil;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@UtilityClass
public class GuavaCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(GuavaCacheManager.class);
    private static Map<String, AbstractLoadingCache> cacheNameToObjectMap = null;

    private static Map<String, AbstractLoadingCache> getCacheMap() {
        if (cacheNameToObjectMap == null) {
            cacheNameToObjectMap = SpringContextUtil.getBeanOfType(AbstractLoadingCache.class);
        }
        return cacheNameToObjectMap;
    }

    private static AbstractLoadingCache<Object, Object> getCacheByName(String cacheName) {
        return getCacheMap().get(cacheName);
    }

    public static Set<String> getCacheNames() {
        return getCacheMap().keySet();
    }

    public static List<CacheStatsDto> getAllCacheStats() {
        Map<String, AbstractLoadingCache> cacheMap = getCacheMap();
        List<String> cacheNameList = new ArrayList<>(cacheMap.keySet());
        Collections.sort(cacheNameList);

        return cacheNameList.stream()
                .map(GuavaCacheManager::getCacheStatsToMap)
                .collect(Collectors.toList());
    }

    private static CacheStatsDto getCacheStatsToMap(String cacheName) {
        AbstractLoadingCache<Object, Object> cache = getCacheByName(cacheName);
        CacheStats cs = cache.getCache().stats();

        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(1);

        CacheStatsDto dto = new CacheStatsDto();
        dto.setCacheName(cacheName);
        dto.setSize(cache.getCache().size());
        dto.setMaximumSize(cache.getMaximumSize());
        dto.setSurvivalDuration(cache.getExpireAfterWriteDuration());
        dto.setHitCount(cs.hitCount());
        dto.setHitRate(percent.format(cs.hitRate()));
        dto.setMissRate(percent.format(cs.missRate()));
        dto.setLoadExceptionCount(cs.loadExceptionCount());
        dto.setLoadSuccessCount(cs.loadSuccessCount());
        dto.setTotalLoadTime(cs.totalLoadTime() / 1000000L);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (cache.getResetTime() != null) {
            dto.setResetTime(df.format(cache.getResetTime()));
        }

        dto.setHighestSize(cache.getHighestSize());
        if (cache.getHighestTime() != null) {
            dto.setHighestTime(df.format(cache.getHighestTime()));
        }

        TimeUnit timeUnit = cache.getTimeUnit();
        if (TimeUnit.SECONDS == timeUnit) {
            dto.setSurvivalDurationUnit("秒");
        } else if (TimeUnit.MINUTES == timeUnit) {
            dto.setSurvivalDurationUnit("分钟");
        } else if (TimeUnit.HOURS == timeUnit) {
            dto.setSurvivalDurationUnit("小时");
        } else if (TimeUnit.DAYS == timeUnit) {
            dto.setSurvivalDurationUnit("天");
        }

        return dto;
    }

    public static void resetCache(String cacheName, String refreshCode) {
        logger.info("GuavaCacheManager resetCache 重置缓存  cacheName {}", cacheName);
        AbstractLoadingCache<Object, Object> cache = getCacheByName(cacheName);

        if (cache == null) {
            logger.warn("GuavaCacheManager refresh 刷新缓存 cacheName = {} 失败, 因为 cacheName 不存在！", cacheName);

        } else if (cache.getRefreshCodeMap().containsKey(refreshCode)) {
            logger.info("GuavaCacheManager resetCache  不刷新缓存 cacheName = {} , 因为 重试刷新 cacheName refreshCode = {}，已经存在！", cacheName, refreshCode);

        } else {
            cache.getRefreshCodeMap().put(refreshCode, "");
            cache.getCache().invalidateAll();
            cache.setResetTime(new Date());
        }
    }

    public static void refresh(String cacheName, String cacheKey, String refreshCode) {

        logger.info("GuavaCacheManager refresh 刷新缓存 cacheName = {}, cacheKey = {}", cacheName, cacheKey);

        AbstractLoadingCache<Object, Object> cache = getCacheByName(cacheName);

        if (cache == null) {
            logger.warn("GuavaCacheManager refresh 刷新缓存 cacheName = {} 失败, 因为 cacheKey = {} 不存在！", cacheName, cacheKey);

        } else if (cache.getRefreshCodeMap().containsKey(refreshCode)) {
            logger.info("GuavaCacheManager resetCache 不刷新缓存 cacheName = {}, 因为 重试刷新 cacheName refreshCode = {}，已经存在！", cacheName, refreshCode);

        } else {
            cache.getRefreshCodeMap().put(refreshCode, "");
            Class<?> clazz = getClassGenericType(cache.getClass(), 0);
            Object key = JSON.parseObject(cacheKey, clazz);
            cache.getCache().refresh(key);
            cache.setResetTime(new Date());
        }
    }

    public static Class<?> getClassGenericType(Class<?> clazz, int index) {
        Type genericType = clazz.getGenericSuperclass();

        if (genericType instanceof ParameterizedType) {

            Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
            if (index < params.length && index >= 0 && params[index] instanceof Class) {
                return (Class<?>) params[index];
            }

        }
        return Object.class;
    }

    public static PageInfo<CacheKeyValue<String, String>> queryDataByPage(int pageNo, String cacheName, String cacheKeyLike) {
        AbstractLoadingCache<Object, Object> cache = getCacheByName(cacheName);
        ConcurrentMap<Object, Object> cacheMap = cache.getCache().asMap();

        PageInfo<CacheKeyValue<String, String>> page = new PageInfo<>();
        page.setCount(cacheMap.size());
        page.setTotal((cacheMap.size() - 1) / page.getPageSize() + 1);

        int startPos = (pageNo - 1) * page.getPageSize() + 1;
        int endPos = pageNo * page.getPageSize();
        int i = 1;

        List<CacheKeyValue<String, String>> list = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : cacheMap.entrySet()) {
            if (startPos <= i && i <= endPos) {
                String key = JSON.toJSONString(entry.getKey());
                if (!isLike(key, cacheKeyLike)) {
                    continue;
                }

                CacheKeyValue<String, String> cacheKeyValue = new CacheKeyValue<>();
                cacheKeyValue.setCacheKey(key);
                cacheKeyValue.setCacheValue(getCacheValue(entry.getValue()));
                list.add(cacheKeyValue);
            }
            i++;
        }

        page.setContent(list);
        return page;
    }

    private static String getCacheValue(Object value) {
        if (null == value) {
            return "";
        }
        String result = JSON.toJSONString(value);
        if (result.length() > 500) {
            return result.substring(0, 500);
        }
        return result;
    }

    private static boolean isLike(String key, String cacheKeyLike) {
        if (cacheKeyLike != null && !StringUtils.isEmpty(cacheKeyLike)) {
            String reg = ".*" + cacheKeyLike + ".*";
            return key.matches(reg);
        } else {
            return true;
        }
    }

    public static CacheKeyValue<String, String> getCacheValueByKey(String cacheName, String cacheKey) {
        AbstractLoadingCache<Object, Object> cache = getCacheByName(cacheName);
        ConcurrentMap<Object, Object> cacheMap = cache.getCache().asMap();

        Class<?> clazz = getClassGenericType(cache.getClass(), 0);
        Object cacheValue = cacheMap.get(JSON.parseObject(cacheKey, clazz));

        CacheKeyValue<String, String> dto = new CacheKeyValue<>();
        dto.setCacheKey(cacheKey);
        dto.setCacheValue(JSON.toJSONString(cacheValue));
        return dto;
    }
}