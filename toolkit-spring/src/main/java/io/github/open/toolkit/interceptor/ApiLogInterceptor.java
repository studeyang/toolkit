package io.github.open.toolkit.interceptor;

import io.github.open.toolkit.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Slf4j
public class ApiLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        accessLog(request);

        return true;
    }

    private void accessLog(HttpServletRequest requestWrapper) {
        log.info("request: {} {}, params: {}", requestWrapper.getMethod(),
                requestWrapper.getRequestURI(), HttpUtils.getRequestParams(requestWrapper));
        if (!HttpMethod.GET.name().equals(requestWrapper.getMethod())) {
            try {
                log.info("request: {} {}, body: {}", requestWrapper.getMethod(),
                        requestWrapper.getRequestURI(), HttpUtils.getRequestBody(requestWrapper));
            } catch (IOException e) {

            }
        }
    }

}
