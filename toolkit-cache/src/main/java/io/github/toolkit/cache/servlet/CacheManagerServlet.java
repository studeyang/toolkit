package io.github.toolkit.cache.servlet;

import io.github.toolkit.cache.dto.CacheKeyValue;
import io.github.toolkit.cache.dto.CacheStatsDto;
import io.github.toolkit.cache.dto.PageInfo;
import io.github.toolkit.cache.guava.GuavaCacheManager;
import io.github.toolkit.cache.util.AllCacheStatsHtmlUtil;
import io.github.toolkit.cache.util.CacheDetailsHtmlUtil;
import io.github.toolkit.cache.util.CacheKeyDetailHtmlUtil;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

public class CacheManagerServlet extends ResourceServlet {

    public CacheManagerServlet() {
        super("static");
    }

    @Override
    protected String process(String url) {
        return null;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String servletPath = request.getServletPath();
        String requestUri = request.getRequestURI();

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String path = requestUri.substring(contextPath.length() + servletPath.length());

        if (path.endsWith(".css")) {
            super.service(request, response);
        } else if (path.contains("cacheDetails")) {
            this.getCacheDetailsByPage(request, response);
        } else if (path.contains("cacheKeyDetail")) {
            this.getCacheKeyDetail(request, response);
        } else if (path.contains("resetCacheName")) {
            this.resetCacheName(request);
            response.sendRedirect("getAllCacheStats");
        } else if (path.contains("refreshCacheKey")) {
            this.refreshCacheKey(request, response);
            response.sendRedirect("getAllCacheStats");
        } else if (path.contains("getAllCacheStats")) {
            this.getAllCacheStats(response);
        } else {
            response.sendRedirect("getAllCacheStats");
        }
    }

    private void getAllCacheStats(HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        List<CacheStatsDto> list = GuavaCacheManager.getAllCacheStats();
        pw.println(AllCacheStatsHtmlUtil.getHtml(list));
    }

    private void refreshCacheKey(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cacheName = request.getParameter("cacheName");

        if (!StringUtils.isEmpty(cacheName)) {

            String cacheKey = request.getParameter("cacheKey");
            if (!StringUtils.isEmpty(cacheKey)) {
                GuavaCacheManager.refresh(cacheName, cacheKey, UUID.randomUUID().toString());
            } else {
                response.sendRedirect("getAllCacheStats");
            }

        } else {
            response.sendRedirect("getAllCacheStats");
        }
    }

    private void resetCacheName(HttpServletRequest request) {
        String cacheName = request.getParameter("cacheName");
        if (!StringUtils.isEmpty(cacheName)) {
            GuavaCacheManager.resetCache(cacheName, UUID.randomUUID().toString());
        }
    }

    private void getCacheKeyDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cacheName = request.getParameter("cacheName");

        if (!StringUtils.isEmpty(cacheName)) {

            String cacheKey = request.getParameter("cacheKey");
            if (!StringUtils.isEmpty(cacheKey)) {
                PrintWriter pw = response.getWriter();
                CacheKeyValue<String, String> cacheKeyValue = GuavaCacheManager.getCacheValueByKey(cacheName, cacheKey);
                pw.println(CacheKeyDetailHtmlUtil.getHtml(cacheKeyValue, cacheName));
            } else {
                response.sendRedirect("getAllCacheStats");
            }

        } else {
            response.sendRedirect("getAllCacheStats");
        }
    }

    private void getCacheDetailsByPage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cacheName = request.getParameter("cacheName");

        if (!StringUtils.isEmpty(cacheName)) {
            String cacheKeyLike = request.getParameter("cacheKeyLike");
            String pageNoStr = request.getParameter("pageNo");

            int pageNo;
            if (!StringUtils.isEmpty(pageNoStr)) {
                pageNo = Integer.parseInt(pageNoStr);
            } else {
                pageNo = 1;
            }
            PageInfo<CacheKeyValue<String, String>> page = GuavaCacheManager.queryDataByPage(pageNo, cacheName, cacheKeyLike);

            PrintWriter pw = response.getWriter();
            pw.println(CacheDetailsHtmlUtil.getHtml(page, cacheName, cacheKeyLike));

        } else {
            response.sendRedirect("getAllCacheStats");
        }
    }
}