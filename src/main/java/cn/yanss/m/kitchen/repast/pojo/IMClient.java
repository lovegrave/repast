package cn.yanss.m.kitchen.repast.pojo;

import lombok.Data;

@Data
public class IMClient{
    String id;
    String name;
    long saveTime;
    /**
     * 过期时间 ms，<=0即不过期
     */
    int expire;
}