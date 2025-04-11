package io.github.toolkit.commons.aop;

import io.github.toolkit.commons.annotation.RequestLog;
import io.github.toolkit.commons.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Slf4j
@Aspect
public class RequestLogAspect {

    @Pointcut("@annotation(io.github.toolkit.commons.annotation.RequestLog)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        // 1. before invoke
        Object targetObj = point.getTarget();
        String className = targetObj.getClass().getName();

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        boolean logResponse = Optional.ofNullable(method.getAnnotation(RequestLog.class))
                .map(RequestLog::responseLog)
                .orElse(false);

        String params = getRequestStr(point, methodSignature);
        log.info("Invoke {}, params: {}", className + "#" + method.getName(), params);

        // 2. invoke
        long start = System.currentTimeMillis();
        Object proceed = point.proceed();

        // 3. after invoke
        String responseInfo = logResponse ? JsonUtil.serializer(proceed) : "";
        log.info("Invoke {}, resp: {}, cost：{}",
                className + "#" + method.getName(),  responseInfo, System.currentTimeMillis() - start);

        return proceed;
    }

    private static String getRequestStr(ProceedingJoinPoint point, MethodSignature methodSignature) {
        String[] parameterNames = methodSignature.getParameterNames();
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();

        Map<String, Object> map = new HashMap<>(8);
        Object[] args = point.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            Class<?> paramClass = parameterTypes[i];
            if (ServletRequest.class.isAssignableFrom(paramClass) || ServletResponse.class.isAssignableFrom(paramClass)) {
                continue;
            }
            map.put(parameterNames[i], args[i]);
        }
        return JsonUtil.serializer(map);
    }

}
