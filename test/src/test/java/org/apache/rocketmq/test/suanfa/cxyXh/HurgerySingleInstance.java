package org.apache.rocketmq.test.suanfa.cxyXh;

public class HurgerySingleInstance {

    private static HurgerySingleInstance instance = new HurgerySingleInstance();

    public HurgerySingleInstance(){

    }
    public static HurgerySingleInstance getInstance(){
        return instance;
    }
}
