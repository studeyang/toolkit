package io.github.toolkit.cache.util;

import io.github.toolkit.cache.dto.CacheKeyValue;
import io.github.toolkit.cache.dto.PageInfo;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.List;

@UtilityClass
public class CacheDetailsHtmlUtil {
    private static String preHtml = "<!DOCTYPE html><html><head><title>本地缓存管理-缓存cacheName详情</title><link rel=\"stylesheet\" type=\"text/css\" href=\"css/bootstrap.min.css\"></head><body><div class=\"col-md-8 col-md-push-2\" style=\"margin-top:50px;\"><p class=\"navbar-text navbar-right\"> <a href=\"getAllCacheStats\" class=\"navbar-link\">返回首页</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"cacheDetails?cacheName=%s\" class=\"navbar-link\">刷新页面</a>&nbsp;&nbsp;</p><form method=\"GET\" action=\"cacheDetails\"><div class=\"input-group input-group-lg\"><span class=\"input-group-addon\">key值</span><input type=\"hidden\" name=\"cacheName\" value=\"%s\"><input type=\"text\" name=\"cacheKeyLike\"class=\"form-control\" placeholder=\"模糊查询key值\"><span class=\"input-group-btn\"><button class=\"btn btn-default\" type=\"submit\">查询</button></form> </span></div><table class=\"table table-striped table-hover table-bordered\"><thead> <tr><th>缓存key</th> <th>缓存Value</th> <th style=\"width:80px\">操作</th> </tr></thead>";
    private static String endHtml = " </div> </body> </html>";
    private static String title = "<span>注：系统刷新，查询key都是使用JSON格式，对简单对象支持会更好！模糊查询现在只支持查询，不支持分页！</span>";

    public static String getHtml(PageInfo<CacheKeyValue<String, String>> page, String cacheName, String cacheKeyLike) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format(preHtml, cacheName, cacheName)).append("<tbody>");
        List<CacheKeyValue<String, String>> list = page.getContent();

        for (CacheKeyValue<String, String> cacheKeyValue : list) {
            sb.append("<tr>");
            sb.append("<td>").append(cacheKeyValue.getCacheKey()).append("</td>");
            sb.append("<td>").append(cacheKeyValue.getCacheValue()).append("</td>");
            sb.append("<td>").append("<a title='显示全部Value值，Value值过大不建议查看！' href='cacheKeyDetail?cacheName=").append(cacheName)
                    .append("&cacheKey=").append(cacheKeyValue.getCacheKey())
                    .append("'>显示全部</a>")
                    .append("<br /><br /><a title='重新加载当前节点缓存，不会清空缓存' href='refreshCacheKey?cacheName=").append(cacheName)
                    .append("&cacheKey=").append(cacheKeyValue.getCacheKey()).append("'>刷新缓存</a>")
                    .append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>").append(getPageHtml(page, cacheName, cacheKeyLike)).append(String.format(title)).append(endHtml);
        return sb.toString();
    }

    private static String getPageHtml(PageInfo<CacheKeyValue<String, String>> page, String cacheName, String cacheKeyLike) {
        StringBuffer sb = new StringBuffer();
        int count = page.getCount();
        if (null != cacheKeyLike && !StringUtils.isEmpty(cacheKeyLike)) {
            return "";
        } else if (count < 1) {
            return "";
        } else {
            int totalPage = page.getTotalPage();
            int pageNo = page.getPageNo();
            sb.append("<nav><ul class=\"pagination\"><li><a title=\"首页\" href=\"cacheDetails?cacheName=").append(cacheName).append("\">&laquo;</a></li>");

            for(int i = pageNo - 4; i <= pageNo + 4; ++i) {
                if (i > 0 && i <= totalPage) {
                    if (i == pageNo) {
                        sb.append("<li class=\"active\"><a href=\"cacheDetails?cacheName=").append(cacheName).append("&pageNo=").append(i).append("\">").append(i).append("</a></li>");
                    } else {
                        sb.append("<li><a href=\"cacheDetails?cacheName=").append(cacheName).append("&pageNo=").append(i).append("\">").append(i).append("</a></li>");
                    }
                }
            }

            sb.append("<li><a title=\"最后一页\" href=\"cacheDetails?cacheName=").append(cacheName).append("&pageNo=").append(totalPage).append("\">&raquo;</a></li></ul></nav>");
            sb.append("<span>总数：").append(count).append("条数据 ，总页数：").append(totalPage).append("页&nbsp;&nbsp;&nbsp;&nbsp;</span>");
            return sb.toString();
        }
    }
}
