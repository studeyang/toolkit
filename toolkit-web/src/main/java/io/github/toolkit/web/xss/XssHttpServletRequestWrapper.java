package io.github.toolkit.web.xss;

import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Objects;


/**
 * @author xiqin.liu
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest orgRequest = null;
    private boolean isIncludeRichText = false;

    public XssHttpServletRequestWrapper(HttpServletRequest request, boolean isIncludeRichText) {
        super(request);
        orgRequest = request;
        this.isIncludeRichText = isIncludeRichText;
    }

    /**
     * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
     */
    @Override
    public String getParameter(String name) {

        Boolean flag = ("content".equals(name) || name.endsWith("WithHtml"));
        if (flag && !isIncludeRichText) {
            return super.getParameter(name);
        }
        name = xssEncode(name);
        String value = super.getParameter(name);
        if (StringUtils.hasText(value)) {
            value = xssEncode(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] arr = super.getParameterValues(name);
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = xssEncode(arr[i]);
            }
        }
        return arr;
    }


    /**
     * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {

        return super.getHeader(name);
    }

    /**
     * 获取最原始的request
     *
     * @return
     */
    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }

    /**
     * 获取最原始的request的静态方法
     *
     * @return
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
        if (req instanceof XssHttpServletRequestWrapper) {
            return ((XssHttpServletRequestWrapper) req).getOrgRequest();
        }

        return req;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(orgRequest.getInputStream()));

        StringBuilder result = new StringBuilder();

        String line = br.readLine();

        while (!Objects.isNull(line)) {
            result.append(line);
            line = br.readLine();
        }

        String xssEncodedStr = xssEncode(result.toString());

        return new WrappedServletInputStream(new ByteArrayInputStream(xssEncodedStr.getBytes()));
    }

    private class WrappedServletInputStream extends ServletInputStream {

        @SuppressWarnings("unused")
        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        private InputStream stream;

        public WrappedServletInputStream(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Do nothing
        }
    }

    private static String xssEncode(String value) {

        if (StringUtils.isEmpty(value)) {
            return value;
        }

        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");

        return value;
    }
}
