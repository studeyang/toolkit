package io.github.open.toolkit.web.xss;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "icec.web.xss.enabled", havingValue = "true", matchIfMissing = true)
public class XssConfig {

    private static final String XSS_EXCLUDES_KEYS = "excludes";

    private static final String XSS_EXCLUDES_VALUES = "/favicon.ico,/img/*,/js/*,/css/*";

    private static final String XSS_IS_INCLUDE_RICH_TEXT_KEY = "isIncludeRichText";

    /**
     * xss过滤拦截器
     */
    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put(XSS_EXCLUDES_KEYS, XSS_EXCLUDES_VALUES);
        initParameters.put(XSS_IS_INCLUDE_RICH_TEXT_KEY, Boolean.TRUE.toString());
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }
}