package org.apache.rocketmq.test.suanfa.cxyXh;

import sun.security.jca.GetInstance;

/**
 * 懒加载单例
 * 懒加载的好处是-当初始化加载资源比较多时，可以到真正使用的时候再加载
 */
public class LazySingleInstance {

    private static  volatile LazySingleInstance instance = null;

    public LazySingleInstance(){

    }

    /**
     * 未使用volatile
     * 此方式存在线程安全问题
     * A B两个线程
     * A B 执行了instance == null 会导致实例初始化两次
     * @return
     */
    public static LazySingleInstance getInstance1(){
        if(instance == null){
            instance = new LazySingleInstance();
        }
        return instance;
    }

    /**
     * 未使用volatile前提下
     * 线程A B
     * 如果线程A 已经执行到了 3的括号里
     * 线程B 刚执行到 1
     * 如果 instance已经存在，则线程B直接返回
     * instance不存在，则进入等待锁
     * 线程A执行实例化的时候，其实有三个步骤
     *  1 分配内存 2 初始化对象 3 设置实例指向分配的内存地址
     *  JVM 编译器存在指令重排序
     * 会导致 执行顺序变为 1 3 2 。有可能执行到3的时候。B判断实例存在直接返回，但是这个时候实例还没有初始化，导致实例不能使用。
     * 为了避免编译器重排序，所以使用volatile关键字进行处理
     *
     * @return
     */
    public static LazySingleInstance getInstance2(){
        if(instance == null){//1
            synchronized(LazySingleInstance.class){
                if(instance == null){//3
                    instance = new LazySingleInstance();
                }
            }
        }
        return instance;
    }
}
