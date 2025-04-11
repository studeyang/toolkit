package io.github.toolkit.example.cache;

import io.github.toolkit.commons.utils.DateTimeUtil;
import io.github.toolkit.cache.guava.AbstractLoadingCache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/9
 */
public class UserCacheImpl extends AbstractLoadingCache<String, UserEntity> {

    public UserCacheImpl() {
        // 最大缓存条数
        setMaximumSize(5);
        setTimeUnit(TimeUnit.DAYS);
        setExpireAfterWriteDuration(37);
    }

    @Override
    public UserEntity get(String key) {
        try {
            return getValue(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserEntity loadData(String key) {
        // 模拟从数据库读取
        UserEntity user = new UserEntity();
        user.setId(key);
        user.setUserName("人员" + key);
        user.setLoadTime(DateTimeUtil.format(new Date()));
        return user;
    }
}
