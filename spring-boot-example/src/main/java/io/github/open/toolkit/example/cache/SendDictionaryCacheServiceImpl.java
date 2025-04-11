package io.github.open.toolkit.example.cache;

import com.google.common.collect.Maps;
import io.github.toolkit.cache.guava.AbstractLoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/9
 */
@Slf4j
public class SendDictionaryCacheServiceImpl extends AbstractLoadingCache<String, Map<String, Map<String, String>>> {

    private static final String ALL_DATA = "all_data";

    public SendDictionaryCacheServiceImpl() {
        // 最大缓存条数
        setMaximumSize(5);
        setTimeUnit(TimeUnit.DAYS);
        setExpireAfterWriteDuration(37);
    }

    @Override
    public Map<String, Map<String, String>> get(String key) {
        try {
            return getValue(ALL_DATA);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Map<String, String>> loadData(String key) {

        Map<String, Map<String, String>> map = Maps.newHashMap();

        Map<String, String> value = Maps.newHashMap();
        value.put("key1", "123");
        map.put(ALL_DATA, value);

        return map;
    }
}
