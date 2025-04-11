package io.github.toolkit.cache.util;

import com.alibaba.fastjson.JSON;
import io.github.toolkit.cache.dto.CacheStatsDto;
import io.github.toolkit.cache.pubsub.jgroup.GuavaCacheJGroup;
import io.github.toolkit.cache.pubsub.IGuavaCachePublisher;
import io.github.toolkit.cache.pubsub.ISubscribeListener;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

@UtilityClass
public class AllCacheStatsHtmlUtil {
    private static final String preHtml = "<!DOCTYPE html><html><head><title>本地缓存管理-数据量统计</title><link rel=\"stylesheet\" type=\"text/css\" href=\"css/bootstrap.min.css\"></head><body><div class=\"col-md-8 col-md-push-2\" style=\"margin-top:50px;\"><p class=\"navbar-text navbar-right\"><a href=\"getAllCacheStats\" class=\"navbar-link\">刷新页面</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><table class=\"table table-striped table-hover table-bordered\"><thead> <tr><th>Cache名称</th> <th>数据量统计</th> <th>命中统计</th> <th>加载统计</th> <th>开始/重置时间</th> <th>操作</th> </tr></thead>";
    private static final String endHtml = "  </div> </body> </html>";

    public static String getHtml(List<CacheStatsDto> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(preHtml).append("<tbody>");

        for (CacheStatsDto dto : list) {
            sb.append("<tr>");
            sb.append("<td>").append(dto.getCacheName()).append("</td>");
            sb.append("<td>").append(sizeStatistics(dto)).append("</td>");
            sb.append("<td>").append(hitStatistics(dto)).append("</td>");
            sb.append("<td>").append(loadStatistics(dto)).append("</td>");
            sb.append("<td>").append(getStaticTime(dto)).append("</td>");
            sb.append("<td>").append("<a  href='cacheDetails?cacheName=").append(dto.getCacheName()).append("'>显示详情</a>").append("<br /><br /><a title='只会清空当前节点缓存' href='resetCacheName?cacheName=").append(dto.getCacheName()).append("'>清空缓存</a>").append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>").append(MemoryUtil.displayMemory().toHtmlString()).append(getChannelHtml()).append(endHtml);
        return sb.toString();
    }

    private static String getChannelHtml() {
        StringBuilder sb = new StringBuilder();
        Map<String, IGuavaCachePublisher> mapPublisher = SpringContextUtil.getBeanOfType(IGuavaCachePublisher.class);
        for (IGuavaCachePublisher publisher : mapPublisher.values()) {
            if (publisher != null) {
                sb.append("<br />redis 发布频道 channel : ").append(publisher.getChannel());
            }
        }

        Map<String, ISubscribeListener> mapSubscribeListener = SpringContextUtil.getBeanOfType(ISubscribeListener.class);
        for (ISubscribeListener subscribeListener : mapSubscribeListener.values()) {
            if (subscribeListener != null) {
                sb.append("<br />redis 订阅频道 channel : ").append(subscribeListener.getChannel());
            }
        }

        Map<String, GuavaCacheJGroup> jGroupMap = SpringContextUtil.getBeanOfType(GuavaCacheJGroup.class);
        for (GuavaCacheJGroup jGroup : jGroupMap.values()) {
            if (jGroup != null) {
                sb.append("<br />JGroup 订阅频道 channel : ").append(JSON.toJSONString(jGroup.getChannel()));
            }
        }

        return sb.toString();
    }

    public static String sizeStatistics(CacheStatsDto obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("当前数据量/上限：").append(obj.getSize()).append("/").append(obj.getMaximumSize());
        sb.append("<br />历史最高数据量：").append(obj.getHighestSize());
        sb.append("<br />最高数据量时间：").append(obj.getHighestTime());
        return sb.toString();
    }

    public static String hitStatistics(CacheStatsDto obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("命中数量：").append(obj.getHitCount());
        sb.append("<br />命中比例：").append(obj.getHitRate());
        sb.append("<br />读库比例：").append(obj.getMissRate());
        return sb.toString();
    }

    public static String loadStatistics(CacheStatsDto obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("成功加载数：").append(obj.getLoadSuccessCount());
        sb.append("<br />失败加载数：").append(obj.getLoadExceptionCount());
        sb.append("<br />总加载毫秒：").append(obj.getTotalLoadTime());
        return sb.toString();
    }

    public static String getStaticTime(CacheStatsDto obj) {
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getResetTime()).append("<br /><br />失效时长：").append(obj.getSurvivalDuration()).append(" ").append(obj.getSurvivalDurationUnit());
        return sb.toString();
    }
}
