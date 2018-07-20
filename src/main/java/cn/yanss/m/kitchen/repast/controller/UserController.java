package cn.yanss.m.kitchen.repast.controller;

import cn.yanss.m.kitchen.repast.pojo.Message;
import cn.yanss.m.kitchen.repast.service.impl.ChannelService;
import cn.yanss.m.kitchen.repast.service.impl.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 长轮询即时通讯Controller
 */
@RestController
@RequestMapping("im")
public class UserController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private ClientService userService;

    /**
     * 长轮询
     * @param req
     * @return
     */
    @PostMapping("poll")
    public DeferredResult poll(HttpServletRequest req){
        return channelService.poll(userService.getIMClient(req.getSession()));
    }

    /**
     * 订阅
     * @param req
     * @param channel
     * @return
     */
    @PostMapping("subscribe")
    public String subscribe(HttpServletRequest req,Message channel){
        /**
         * 测试时User的作用仅仅是作为map的key所以new一个即可
         */
        channelService.subscribe(channel.getChannel(),userService.getIMClient(req.getSession()));
        return "订阅: "+channel.getChannel();
    }

    /**
     * 取消订阅
     * @param req
     * @param channel
     * @return
     */
    @PostMapping("unsubscribe")
    public String unsubscribe(HttpServletRequest req,Message channel){
        if (channel != null&&channel.getChannel()!=null) {
            /**
             * 测试时User的作用仅仅是作为map的key所以new一个即可
             */
            channelService.unsubscribe(channel.getChannel(),userService.getIMClient(req.getSession()));
            return "取消订阅:"+channel.getChannel();
        }else{
            /**
             * 测试时User的作用仅仅是作为map的key所以new一个即可
             */
            channelService.unsubscribe(userService.getIMClient(req.getSession()));
            return "取消订阅全部频道";
        }
    }

    /**
     * 似乎识别不了内部的自定义对象CommonMessage,需要HTTP请求设置contentType: "application/json",
     * 把json转化为字符串传给data,并且此方法设置@RequestBody
     * @param req
     * @param msg
     * @return
     */
    @PostMapping("emit")
    public String emit(HttpServletRequest req, @RequestBody Message msg){
        msg.setSender(userService.getIMClient(req.getSession()));
        channelService.emit(msg.getChannel(),msg.getSender(),msg.getMessage());
        return "发送成功";
    }
}
