package org.apache.rocketmq.test.suanfa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CollectSty {

    /**
     * arrayList学习
     * arrayList使用数组实现
     * 如果初始化指定了长度为0，1.8直接返回一个空静态空数组。之前的是new一个。效率差些
     *
     * 数组元素用transient修饰，这个关键字是java自带默认进制进行序列化。
     * 屏蔽null字段，只序列化有值的字段。避免空间浪费
     *
     * jdk1.8 扩容1.5 通过移位>>1 计算0.5是多少。之前是 *3/2
     * 增加 删除 扩容 缩小容量的时候 就进行modcount++，实现
     * fast-fail机制
     *
     * trimToSize 使集合缩容到实际大小。进行内存释放
     */
    @Test
    public void arryListSty(){
        List<String> list = new ArrayList<>();
        list.add("abc");
//        Object
    }

    /**
     * linkedList学习
     *
     */
    public void LinkedListStry(){

    }


}
