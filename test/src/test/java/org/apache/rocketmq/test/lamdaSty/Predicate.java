package org.apache.rocketmq.test.lamdaSty;

public interface Predicate<T> {
    boolean test(T t);
}
