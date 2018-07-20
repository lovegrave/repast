package cn.yanss.m.kitchen.repast.pojo;

import lombok.Data;

import java.util.Set;

@Data
public class Channel {
    String name;
    Set<IMClient> subscriptionSet;

    public Channel(String name, Set subscriptionSet) {
        this.name = name;
        this.subscriptionSet = subscriptionSet;
    }

    public Channel() {
    }
}
