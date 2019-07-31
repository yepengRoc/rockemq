package org.apache.rocketmq.test.jdkSource;

public class MyList<T> {

    private transient Object[] element;

    private static final int defaultArrLen = 10;

    private int arrLen = 0;
    MyList(){
        element = new Object[defaultArrLen];
    }
    MyList(int len){
        if(len < 0){
            throw new RuntimeException("请设置大于0的整数");
        }else{
            element = new Object[len];
        }
    }

    public boolean add(T t){

        return true;
    }

    public boolean remove(T t){
        return true;
    }

    /**
     * 根据索引移除元素。
     * 考虑索引是否越界。如果在中间前则从前面遍历。如果在中间点后，则从后面遍历
     * @param index
     * @return
     */
    public boolean remove(int index){
        return true;
    }

    public Object[] expandCapation(Object[] srcArr,int len){
        return null;
    }


}
