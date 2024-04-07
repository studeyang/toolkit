package io.github.open.toolkit.web.xss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XssFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

    /**
     * 是否过滤富文本内容
     */
    private boolean isIncludeRichText = true;

    private List<String> excludes = new ArrayList<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("xss filter is open");
        }

        if (handleExcludeURL((HttpServletRequest) request)) {
            filterChain.doFilter(request, response);
            return;
        }

        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request, isIncludeRichText);

        filterChain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request) {

        if (excludes == null || excludes.isEmpty()) {
            return false;
        }

        String url = request.getServletPath();

        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("xss filter init ====================");
        }
        String isIncludeRichTextStr = filterConfig.getInitParameter("isIncludeRichText");
        if (StringUtils.hasText(isIncludeRichTextStr)) {
            isIncludeRichText = Boolean.parseBoolean(isIncludeRichTextStr);
        }

        String tempExcludes = filterConfig.getInitParameter("excludes");

        if (StringUtils.hasText(tempExcludes)) {

            for (int i = 0; tempExcludes.split(",") != null && i < tempExcludes.split(",").length; i++) {
                excludes.add(tempExcludes.split(",")[i]);
            }
        }
    }

    @Override
    public void destroy() {

        logger.debug("(XssFilter) destroy...");
    }

}