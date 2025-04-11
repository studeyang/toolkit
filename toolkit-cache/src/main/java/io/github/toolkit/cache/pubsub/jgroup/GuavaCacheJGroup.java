package io.github.toolkit.cache.pubsub.jgroup;

import io.github.toolkit.cache.dto.GuavaCacheJGroupDto;
import io.github.toolkit.cache.exception.CacheException;
import org.apache.commons.lang.StringUtils;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GuavaCacheJGroup implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(GuavaCacheJGroup.class);
    private static final String CLUSTER_NAME = "guava-cache-cluster";
    private final ThreadPoolExecutor executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    private JChannel channel;
    private String channelName;
    private JGroupReceiverAdapter receiverAdapter;
    private JGroupChannelListener channelListener;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("init cluster {} channel {} start !", CLUSTER_NAME, this.channelName);
        this.initChannel();
        log.info("init cluster {} channel {} end !", CLUSTER_NAME, this.channelName);
    }

    public void initChannel() throws Exception {
        this.channel = new JChannel();
        this.channel.setName(this.channelName);
        this.channel.receiver(this.receiverAdapter);
        this.channel.addChannelListener(this.channelListener);
        this.channel.connect(CLUSTER_NAME);
    }

    public void sendMsg(final GuavaCacheJGroupDto dto) {
        if (StringUtils.isEmpty(dto.getRefreshCode())) {
            throw CacheException.REFRESHCODE_FAIL;
        } else {
            this.send(dto);
            this.executors.execute(() -> {
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    log.error("休眠异常！", e);
                    Thread.currentThread().interrupt();
                }
                this.send(dto);
            });
        }
    }

    private void send(GuavaCacheJGroupDto dto) {
        try {
            Message msg = new Message();
            msg.setBuffer(Util.objectToByteBuffer(dto));
            this.channel.send(msg);
        } catch (Exception e) {
            log.error("GuavaCacheJGroup send msg error ! message obj ：{}", dto, e);
        }
    }

    public JChannel getChannel() {
        return this.channel;
    }

    @Override
    public void destroy() {
        log.info("close cluster {} channel {} start !", CLUSTER_NAME, this.channelName);
        this.channel.close();
        log.info("close cluster {} channel {} end !", CLUSTER_NAME, this.channelName);
    }
}
