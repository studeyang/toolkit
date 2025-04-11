package io.github.toolkit.cache.util;

import io.github.toolkit.cache.dto.CacheKeyValue;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CacheKeyDetailHtmlUtil {
    private static final String preHtml = "<!DOCTYPE html><html><head><title>本地缓存管理-缓存key详情</title><link rel=\"stylesheet\" type=\"text/css\" href=\"css/bootstrap.min.css\"></head><body><div class=\"col-md-8 col-md-push-2\" style=\"margin-top:50px;\"><p class=\"navbar-text navbar-right\"> <a href=\"getAllCacheStats\" class=\"navbar-link\">返回首页</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"cacheDetails?cacheName=%s\" class=\"navbar-link\">返回缓存Value</a>&nbsp;&nbsp;&nbsp;&nbsp;</p><table class=\"table table-striped table-hover table-bordered\"><thead> <tr><th>缓存key</th> <th>缓存Value</th> </tr></thead>";
    private static String endHtml = " </div> </body> </html>";

    public static String getHtml(CacheKeyValue<String, String> cacheKeyValue, String cacheName) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(preHtml, cacheName)).append("<tbody>");
        sb.append("<tr>");
        sb.append("<td>").append(cacheKeyValue.getCacheKey()).append("</td>");
        sb.append("<td>").append(cacheKeyValue.getCacheValue()).append("</td>");
        sb.append("</tr>");
        sb.append("</tbody></table>").append(endHtml);
        return sb.toString();
    }
}
