package io.github.toolkit.cache.pubsub.jgroup;

import org.jgroups.Channel;
import org.jgroups.ChannelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGroupChannelListener implements ChannelListener {

    private static final Logger log = LoggerFactory.getLogger(JGroupChannelListener.class);

    @Override
    public void channelConnected(Channel channel) {
        log.info("连接，通道名称：{}，集群名称：{}", channel.getName(), channel.getClusterName());
    }

    @Override
    public void channelDisconnected(Channel channel) {
        log.warn("断开，通道名称：{}，集群名称：{}", channel.getName(), channel.getClusterName());

        try {
            log.info("即将进行重连。。。。。");
            channel.connect(channel.getClusterName());
        } catch (Exception var6) {
            log.error("重连发生异常！", var6);
        } finally {
            if (channel.isConnecting()) {
                log.info("重连进行中！");
            } else {
                log.info("重连{}", channel.isConnected() ? "成功！" : "失败！");
            }
        }
    }

    @Override
    public void channelClosed(Channel channel) {
        log.warn("关闭，通道名称：{}，集群名称：{}", channel.getName(), channel.getClusterName());
    }

}
