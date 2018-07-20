package cn.yanss.m.kitchen.repast.pojo;

import lombok.Data;

@Data
public class Message {

    private String channel;

    private IMClient sender;

    private CommonMessage message;
}
