package cn.yanss.m.kitchen.repast.service.impl;

import cn.yanss.m.kitchen.repast.pojo.Channel;
import cn.yanss.m.kitchen.repast.pojo.CommonMessage;
import cn.yanss.m.kitchen.repast.pojo.IMClient;
import cn.yanss.m.kitchen.repast.pojo.IMMessageQueue;
import cn.yanss.m.kitchen.repast.service.IChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChannelService implements IChannelService {

    private final ClientService clientService;

    /**
     * 客户端ID与消息队列映射
     */
    private final Map<String,IMMessageQueue> resultMap = new ConcurrentHashMap<>();
    /**
     * 客户端ID与频道的映射
     */
    private final Map<String,Channel> channelMap = new ConcurrentHashMap<>();
    @Autowired
    public ChannelService(ClientService imUserService) {
        this.clientService = imUserService;
    }
    @Override
    public void subscribe(String channelName, IMClient client) {
        Channel channel = channelMap.get(channelName);
        if (channel==null) {
            channel = new Channel(channelName,new HashSet<>());
            channelMap.put(channelName,channel);
        }
        channel.getSubscriptionSet().add(client);
    }
    @Override
    public void unsubscribe(String channelName, IMClient client) {
        Channel channel = channelMap.get(channelName);
        if (channel==null) {
            return;
        }
        Set subscriptionSet = channel.getSubscriptionSet();
        subscriptionSet.remove(client);
        if(subscriptionSet.size()==0){
            channelMap.remove(channelName);
        }
    }
    @Override
    public void unsubscribe(IMClient client) {
        if (client != null) {
            channelMap.values().forEach(channel -> channel.getSubscriptionSet().remove(client));
        }
    }
    @Override
    public void emit(String channelName, IMClient sender , CommonMessage data) {
        /**
         * 发送消息时候更新savetime
         */
        sender.setSaveTime(System.currentTimeMillis() + clientService.getExpire());
        Channel channel = channelMap.get(channelName);
        if (channel!=null) {
            /**
             * 当已达到过期时间 删除此client,并且从resultMap中删除对应的消息
             */
            channel.getSubscriptionSet().forEach(imUser -> {
                boolean isExpired = clientService.isExpired(imUser);
                if(isExpired){
                    channel.getSubscriptionSet().remove(imUser);
                    resultMap.remove(imUser.getId());
                }
            });
            channel.getSubscriptionSet().forEach(client-> send(client,data));
            /**
             * /当channel中没有订阅者，删除此channel
             */
            if( channel.getSubscriptionSet().size()==0){
                channelMap.remove(channelName);
            }
        }
    }
    @Override
    public DeferredResult poll(IMClient receiver){
        IMMessageQueue queue = resultMap.get(receiver.getId());
        if (queue==null) {
            queue = new IMMessageQueue();
            resultMap.put(receiver.getId(),queue);
        }
        return queue.poll();
    }
    private void send(IMClient receiver, CommonMessage message){
        IMMessageQueue queue = resultMap.get(receiver.getId());
        if (queue != null) {
            queue.send(message);
        }
    }
}
