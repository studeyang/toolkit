package io.github.open.toolkit.web.security;

import io.github.open.toolkit.commons.utils.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 一个提取身份的拦截器，抽象了获取身份信息的方法
 * @see Oauth2AuthenticationExtractor
 * @see JwtAuthenticationExtractor
 *
 * @author jian.xu
 * @version v1.2_20200520
 */
public class AuthenticationResolveInterceptor extends HandlerInterceptorAdapter implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResolveInterceptor.class);

    private static final String MANAGER_PATH_KEY = "management.contextPath";

    private final List<HttpAuthenticationExtractor> extractors;
    private final List<String> ignorePatterns = Arrays.asList("/public/**", "/static/**");

    public AuthenticationResolveInterceptor(List<HttpAuthenticationExtractor> extractors) {

        Assert.notNull(extractors);
        this.extractors = new LinkedList<>(extractors);
    }

    @Override
    public void setEnvironment(Environment environment) {

        Assert.notNull(environment);

        if (environment.containsProperty(MANAGER_PATH_KEY)) {
            String managerPath = environment.getProperty(MANAGER_PATH_KEY);
            if (StringUtils.hasText(managerPath)) {
                this.ignorePatterns.add(managerPath + "/**");
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.debug("requestUrl='{}'", request.getRequestURI());

        SecurityContextHolder.getContext().setAuthentication(null);

        if (this.ignoredRequest(request)) {
            LOG.warn("忽略的请求。");

        } else {

            for (HttpAuthenticationExtractor extractor : extractors) {
                if (extractor.supportRequest(request)) {
                    Authentication auth = extractor.extractAuthentication(request);
                    if (null != auth) {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        return true;
                    }
                }
            }

            LOG.warn("无法获取到用户的登入信息！请求地址：" + request.getRequestURI());
        }
        return true;
    }

    private boolean ignoredRequest(HttpServletRequest request) {
        return RequestMethod.HEAD.name().equals(request.getMethod()) || RequestUtil.matchs(request, (String[])this.ignorePatterns.toArray(new String[0]));
    }
}
