package com.yzw;

/**
 * Created by yzw on 2016/4/9 0009.
 */
public class Subscription {
    public String tag;
    public Subscriber<Object> subscriber;
    public int priority;
    public ThreadMode threadMode;

    public Subscription(String tag, Subscriber<Object> subscriber, int priority, ThreadMode threadMode) {
        this.tag = tag;
        this.subscriber = subscriber;
        this.priority = priority;
        this.threadMode = threadMode;
    }
}
