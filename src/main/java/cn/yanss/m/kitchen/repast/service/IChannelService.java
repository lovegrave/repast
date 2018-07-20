package cn.yanss.m.kitchen.repast.service;

import cn.yanss.m.kitchen.repast.pojo.CommonMessage;
import cn.yanss.m.kitchen.repast.pojo.IMClient;
import org.springframework.web.context.request.async.DeferredResult;

public interface IChannelService {

    void subscribe(String channelName, IMClient client);

    void unsubscribe(String channelName, IMClient client);

    void unsubscribe(IMClient client);

    void emit(String channelName, IMClient sender , CommonMessage data);

    DeferredResult poll(IMClient receiver);



}
