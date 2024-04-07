//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.open.toolkit.web;

import io.github.open.toolkit.commons.format.TimestampFormatter;
import io.github.open.toolkit.commons.web.handler.AccessInfoHandlerInterceptor;
import io.github.open.toolkit.web.accessinfo.WebBaseAccessInfoBuilder;
import io.github.open.toolkit.web.security.AuthenticationResolveInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Iterator;
import java.util.List;

public class GenericWebMvcConfig extends WebMvcConfigurerAdapter {

    protected static final String[] STATIC_PATH_PATTERNS = new String[]{"/js/**", "/app/**", "/css/**", "/images/**", "/static/**", "/webjars/**"};
    protected static final String[] SPECIAL_PATH_PATTERNS = new String[]{"/userinfo", "/userinfo/agent"};

    @Autowired(required = false)
    private AuthenticationResolveInterceptor authenticationResolveInterceptor;

    public GenericWebMvcConfig() {
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer c) {
        c.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(this.getAccessInfoHandlerInterceptor())
                .addPathPatterns(new String[]{"/**"})
                .excludePathPatterns(STATIC_PATH_PATTERNS)
                .excludePathPatterns(SPECIAL_PATH_PATTERNS);

        if (null != this.authenticationResolveInterceptor) {
            registry.addInterceptor(this.authenticationResolveInterceptor)
                    .addPathPatterns(new String[]{"/**"})
                    .excludePathPatterns(STATIC_PATH_PATTERNS);
        }

    }

    protected AccessInfoHandlerInterceptor getAccessInfoHandlerInterceptor() {
        AccessInfoHandlerInterceptor accessInfoHandlerInterceptor = new AccessInfoHandlerInterceptor();
        accessInfoHandlerInterceptor.setAccessInfoProvider(new WebBaseAccessInfoBuilder());
        return accessInfoHandlerInterceptor;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Iterator it = converters.iterator();

        while (it.hasNext()) {
            HttpMessageConverter<?> converter = (HttpMessageConverter) it.next();
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setSupportedMediaTypes(MediaType.parseMediaTypes("application/json;charset=utf-8,text/plain;charset=utf-8"));
            }
        }

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new TimestampFormatter());
    }



}
