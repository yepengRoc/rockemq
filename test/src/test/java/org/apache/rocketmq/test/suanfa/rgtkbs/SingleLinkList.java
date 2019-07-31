package org.apache.rocketmq.test.suanfa.rgtkbs;

import java.util.concurrent.LinkedTransferQueue;

/**
 * 如果天空不死
 */
public class SingleLinkList {

    private Node header;

    private Node tail;

    private int size;

    private int modeCount;

    class Node{
        public Object val;

        public Node next;

        Node(Object val,Node next){
            this.val = val;
            this.next = next;
        }
    }
    SingleLinkList(){
        header = tail = new Node(null, null);
    }

    public boolean add(Object obj){
        if(null == obj){
           return false;
        }


        Node node = new Node(obj,null);
        tail.next = node;
        tail = node;
        size++;
        return true;
    }

    /**
     * 指定位置插入元素
     * @param idx
     * @param obj
     * @return
     */
    public boolean add(int idx,Object obj){

        if(idx < 0 ||idx > size){
            return false;
        }
        Node insertNode = new Node(obj,null);
        int i = 0;
        Node preNode = header;
        Node currNode = header;
        while(i <= idx ){
            preNode = currNode;
            currNode = currNode.next;
            i++;
        }
        preNode.next = insertNode;
        insertNode.next = currNode;
        return  true;
    }

    /**
     * 删除指定值
     * @param obj
     * @return
     */
    public boolean del(Object obj){
        Node preNode = header;
        Node node = header;
        while(!node.val.equals(obj)){
            preNode = node;
            node = node.next;
        }
        if(node == null){
            return false;
        }
        preNode.next = node.next;
        node.next = null;
        if(node == tail){
            tail = preNode;
        }
        return true;
    }

    /**
     * 删除指定位置的元素
     * @param idx
     * @return
     */
    public boolean del(int idx){
        if(idx < 0 || idx > size){
            return false;
        }
        int i = 0;
        Node node = header;
        Node preNode = header;
        while(i <= idx){
            preNode = node;
            node = node.next;
        }
        preNode.next = node.next;
        node.next = null;
        if(node == tail){
            tail = preNode;
        }
        return true;
    }


    public static void main(String[] args) {
        LinkedTransferQueue ltq = new LinkedTransferQueue();

    }



}
