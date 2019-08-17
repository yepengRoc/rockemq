package org.apache.rocketmq.test.jdkSource;

import org.omg.CORBA.OBJ_ADAPTER;

import java.util.HashMap;
import java.util.Map;

/**
 * 集合学习
 */
public class CollectStyTest {


    public void hashMapSty(){
        Map<String, Object> map = new HashMap<>();
        map.put("","");
        map.putAll(new HashMap<>());
        map.remove("");
        map.get("");
        map.containsKey("");

    }
}
