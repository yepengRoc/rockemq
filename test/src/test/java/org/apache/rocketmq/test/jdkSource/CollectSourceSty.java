package org.apache.rocketmq.test.jdkSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectSourceSty {
    /**
     * ArrayList学习
     */
    public void arrayListSty(){
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.indexOf(1);
        list.set(0, 2);
    }

    /**
     * LinkedList学习
     */
    public void linkedListSty(){
        LinkedList<Integer> list = new LinkedList<>();
        list.add(1);
        list.indexOf(1);
        list.set(0, 2);
    }

    /**
     * CopyOnWriteArrayList 学习
     */
    public void copyOnWriterArrayListSty(){
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.indexOf(1);
        list.set(0, 2);
        list.contains(1);
    }

    /**
     * HashMap学习
     */
    public void hasMapSty(){

    }

    /**
     * LinkedHashMap学习
     */
    public void linkedHashMapSty(){

    }
    public void concurrentHashMapSty(){
        ConcurrentHashMap<String, Integer> concurHashMap = new ConcurrentHashMap<>();
    }

    /**
     * WeakHashMap学习
     */
    public void weakHashMapSty(){
        WeakHashMap weakHashMap = new WeakHashMap();
    }

    /**
     * TreeMap学习
     */
    public void treeMapSty(){

    }
    public void hashSetSty(){
        
    }
    public void linkedHashSetSty(){
        
    }
    public void treeSetSty(){
        
    }
    public void copyOnWriteArraySetSty(){
        
    }
    public void concurrentSkipListSetSty(){
        
    }
   /* public void priorityQueueSty(){

    }*/
    public void PriorityQueueSty(){
        //优先队列，一般会指定业务排序规则
        PriorityQueue que = new PriorityQueue(10, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return 0;
            }
        });
        que.add(1);
    }
    public void ArrayBlockingQueueSty(){

    }
    public void LinkedBlockingQueueSty(){

    }
    public void SynchronousQueueSty(){

    }
    public void PriorityBlockingQueueSty(){

    }
    public void LinkedTransferQueueSty(){

    }
    public void ConcurrentLinkedQueueSty(){

    }
    public void DelayQueueSty(){

    }
    public void ArrayDequeSty(){

    }
    


//— ConcurrentSkipListSet
// PriorityQueue

// ArrayBlockingQueue

// LinkedBlockingQueue

// SynchronousQueue

// PriorityBlockingQueue

// LinkedTransferQueue

// ConcurrentLinkedQueue

// DelayQueue
// ArrayDeque
//
// LinkedList


}
