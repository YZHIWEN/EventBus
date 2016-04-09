package com.yzw;

/**
 * Created by yzw on 2016/4/9 0009.
 */
public class Subscription {
    String tag;
    public Subscriber<Object> subscriber;
    int priority;

    public Subscription(String tag, Subscriber<Object> subscriber, int priority) {
        this.tag = tag;
        this.subscriber = subscriber;
        this.priority = priority;
    }
}
