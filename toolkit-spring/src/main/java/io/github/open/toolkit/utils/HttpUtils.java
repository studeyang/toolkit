package io.github.open.toolkit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtils {

    /**
     * 获取 Body 参数
     *
     * @param request request
     * @return body字符串
     */
    public static String getRequestBody(final HttpServletRequest request) throws IOException {
        try (InputStream inputStream = request.getInputStream()) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        try (org.apache.commons.io.output.ByteArrayOutputStream output = new ByteArrayOutputStream();) {
            copyLarge(input, output, new byte[1024 * 4]);
            return output.toByteArray();
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 将URL请求参数转换成SortedMap
     *
     * @param request request
     */
    public static SortedMap<String, String> getRequestParams(HttpServletRequest request) {

        SortedMap<String, String> result = new TreeMap<>();

        getParams(request, result);

        return result;
    }

    public static SortedMap<String, String> getHeaders(HttpServletRequest request) {

        SortedMap<String, String> result = new TreeMap<>();

        Enumeration<?> enumeration = request.getHeaderNames();

        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = request.getHeader(key);
                if (Objects.nonNull(value)) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private static void getParams(HttpServletRequest request, Map<String, String> result) {
        Enumeration<?> temp = request.getParameterNames();

        if (null != temp) {

            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();

                String value = request.getParameter(en);

                if (Objects.nonNull(value)) {

                    result.put(en, value);

                }
            }
        }

    }

}
