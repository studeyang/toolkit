package io.github.toolkit.cache.pubsub.jgroup;

import io.github.toolkit.cache.dto.GuavaCacheJGroupDto;
import io.github.toolkit.cache.guava.GuavaCacheManager;
import org.apache.commons.lang.StringUtils;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JGroupReceiverAdapter extends ReceiverAdapter {
    private static final Logger log = LoggerFactory.getLogger(JGroupReceiverAdapter.class);

    @Override
    public void receive(Message msg) {
        try {
            log.info("接收到消息来自：{}", msg.getSrc());
            log.info("Message Object ：{}", msg);
            GuavaCacheJGroupDto message = (GuavaCacheJGroupDto)Util.objectFromByteBuffer(msg.getBuffer());
            log.info("GuavaCacheJGroupReceiverAdapter receive , message =  {}", message);
            if (message == null) {
                log.warn("GuavaCacheJGroupReceiverAdapter receive error, because of message is null");
                return;
            }

            if (StringUtils.isEmpty(message.getCacheName())) {
                log.warn("GuavaCacheJGroupReceiverAdapter receive error, because of message.getCacheName is null");
                return;
            }

            if (StringUtils.isEmpty(message.getCacheKey())) {
                GuavaCacheManager.resetCache(message.getCacheName(), message.getRefreshCode());
                return;
            }

            if (!StringUtils.isEmpty(message.getCacheKey())) {
                GuavaCacheManager.refresh(message.getCacheName(), message.getCacheKey(), message.getRefreshCode());
            }
        } catch (Exception var3) {
            log.error("GuavaCacheJGroupReceiverAdapter receive", var3);
        }

    }

    @Override
    public void viewAccepted(View view) {
        log.info("集群成员发生变化！成员列表如下：");
        List<Address> members = view.getMembers();

        for (Address mbr : members) {
            log.info("member:{}", mbr);
        }

    }

    @Override
    public void suspect(Address suspected_mbr) {
        log.info("发生可疑成员：{}", suspected_mbr);
    }

    @Override
    public void block() {
        log.info("block !!!");
    }

    @Override
    public void unblock() {
        log.info("unblock !!!");
    }
}
