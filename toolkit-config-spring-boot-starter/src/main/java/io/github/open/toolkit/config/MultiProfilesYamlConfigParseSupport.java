package io.github.open.toolkit.config;

import com.alibaba.nacos.spring.util.parse.DefaultYamlConfigParse;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * for {@link com.alibaba.nacos.spring.util.ConfigParseUtils} line 60: ServiceLoader.load
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @date 2022/1/20
 */
public class MultiProfilesYamlConfigParseSupport extends DefaultYamlConfigParse {

    private static Set<String> PROFILES;

    public static void initSpringProfiles(String[] activeProfiles, String[] defaultProfiles) {
        if (null == PROFILES) {
            if (activeProfiles.length > 0) {
                PROFILES = Arrays.stream(activeProfiles).collect(Collectors.toSet());
            } else {
                PROFILES = Arrays.stream(defaultProfiles).collect(Collectors.toSet());
            }
        }
    }

    @Override
    public Map<String, Object> parse(String configText) {
        final AtomicReference<Map<String, Object>> result = new AtomicReference<>();
        process(map -> {
            if (result.get() == null) {
                result.set(map);
            } else {
                Object obj = map.remove("spring.profiles");
                if (null == obj || isActiveProfile(obj)) {
                    result.get().putAll(map);
                }
            }
        }, createYaml(), configText);
        return result.get();
    }

    private boolean isActiveProfile(Object pObj) {
        for (String profile : PROFILES) {
            if (profile.equals(pObj)) {
                return true;
            }
        }
        return false;
    }
}
