package org.apache.rocketmq.test.suanfa.cxyXh;

public class VolatileTest {
    /**
     * 主内存：
     *  所有线程共享
     *工作内存：
     *  cpu 缓存（寄存器）。每个线程有自己的工作内存，是对主存数据的拷贝
     *
     * 如果避免这种情况：
     *  可以使用synchronized --但是这种方式比较重。
     *   轻量级volatile
     *  volatile只保证可见性，不保证原子性。多个volatile复合操作，不保证安全。
     *      其中比较典型的的值 变量++ 操作。
     *          getstatic //读取静态变量（count）
     *          iconst_1 //定义常量1
     *          iadd //count 增加1  不具备原子操作
     *          putstatic //把count结果同步到主存
     *  可见性：只要有一个线程修改了变量，立即会同步到主存，同时使其它工作内存中的副本失效
     *  为什么有这样的特性，是因为java的happer-before（先行发生原则） 保证
     *   通俗：如果一个事件发生在另一个事件之前，其结果必须映射，即便事件（指各种指令）的执行顺序是乱序的
     *valotile适用场景：
     *  1运行结果不依赖当前值，或者只有单一线程操作
     *  2变量不需要其它状态变量共同参与不变约束  votatile a ;volatile b
     * 指令重排
     *   jvm编译java代码，或cpu执行jvm字节码时，对现有指令进行重排
     *   不改变执行结果的前提下，优化运行效率。这里执行结果：指单线程下的执行结果
     *通过内存屏障阻止指令重排
     *  LoadLoad屏障
     *      r1 屏障 r2 保证 r1 肯定在r2前执行完
     *  StoreStore屏障
     *      w1 屏障 w2 保证 w1 写操作对其它处理器可见，且在w2之前执行
     *  LoadStore屏障
     *   r1 屏障 w1 r1读数据完毕才会执行w1写
     *  StoreLoad屏障
     *   w1 屏障 r1 w1写操作之后，数据对其它处理器可见，才有执行r1读。此屏障开销最大
     *
     * 写前加 StoreStore 写后加 storeLoad
     * 读前加 LoadLoad 读后加LoadStore
     * Happer-before是jsr-133规范。 内存屏障是机器指令，是happer-before的实现
     * 结合volatile进行说明：
     * volatile 对于 long 和double 8字节的解决方式
     */
    public void volatileExplain(){

    }
}
