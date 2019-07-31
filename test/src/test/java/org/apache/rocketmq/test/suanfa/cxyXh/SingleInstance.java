package org.apache.rocketmq.test.suanfa.cxyXh;

/**
 * 使用枚举，饿汉模式。加载所有资源。但是安全 反射也不行
 */
public enum SingleInstance {
    INSTANCE;
}
