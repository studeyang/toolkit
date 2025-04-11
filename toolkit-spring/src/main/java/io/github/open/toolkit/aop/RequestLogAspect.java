package io.github.open.toolkit.aop;

import io.github.open.toolkit.utils.HttpUtils;
import io.github.open.toolkit.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Stack;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Slf4j
@Aspect
public class RequestLogAspect {

    @Pointcut("@annotation(io.github.open.toolkit.annotation.RequestLog)")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void doBefore(JoinPoint pjp) {

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            String params = JsonUtils.serializer(HttpUtils.getRequestParams(request));
            String method = request.getMethod();

            if (HttpMethod.GET.name().equals(method)) {
                log.info("GET {}, params: {}", request.getRequestURI(), params);
            } else {
                String body = zip(HttpUtils.getRequestBody(request));
                log.info("{} {}, params: {}, body: {}", method, request.getRequestURI(), params, body);
            }

        } catch (Exception e) {
            log.error("Http 请求参数解析失败.", e);
        }
    }

    private String zip(String requestBody) {
        if (requestBody == null) {
            return null;
        }
        // 删除多余的空格，回车符
        Stack<Character> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();
        for (char c : requestBody.toCharArray()) {
            if (stack.isEmpty()) {
                if (c == '"') {
                    stack.push(c);
                    builder.append(c);
                } else if (c != ' ' && c != '\r' && c != '\n') {
                    builder.append(c);
                }
            } else {
                if (c == '"') {
                    stack.pop();
                }
                builder.append(c);
            }
        }
        return builder.toString();
    }

}
