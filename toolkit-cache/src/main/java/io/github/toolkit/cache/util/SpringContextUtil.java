package io.github.toolkit.cache.util;

import io.github.toolkit.cache.exception.CacheException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/11
 */
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        if (applicationContext == null) {
            throw CacheException.SPRINGCONTEXTUTIL_FAIL;
        } else {
            return applicationContext.getBean(name);
        }
    }

    public static <T> Map<String, T> getBeanOfType(Class<T> type) {
        if (applicationContext == null) {
            throw CacheException.SPRINGCONTEXTUTIL_FAIL;
        }
        return applicationContext.getBeansOfType(type);
    }
}
