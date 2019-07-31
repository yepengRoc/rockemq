package org.apache.rocketmq.test.jdkSource;

/**
 * 单向链表
 */
public class DoubleLinkList<T> {

    private int arrLen = 0;

    private int modCount = 0;

    private int threshold;//阈值


    private class Node{

        private T value;

        private Node next;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
