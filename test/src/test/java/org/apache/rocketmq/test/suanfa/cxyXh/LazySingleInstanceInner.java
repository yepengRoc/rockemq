package org.apache.rocketmq.test.suanfa.cxyXh;

/**
 * 懒加载单例
 * 懒加载的好处是-当初始化加载资源比较多时，可以到真正使用的时候再加载
 * 这个方法就可以做到这一点
 * 但是可以利用反射，再次创建一个对象
 */
public class LazySingleInstanceInner {


    public LazySingleInstanceInner(){

    }

    private  static class InstanceHold{
        private   static  final LazySingleInstanceInner instance = new LazySingleInstanceInner();
        public InstanceHold(){
        }
    }

    /**
     * 使用内部类进行实例化
     * 通过classloader的加载机制来实现懒加载
     * @return
     */
    public static LazySingleInstanceInner getInstance(){

        return InstanceHold.instance;
    }

}
