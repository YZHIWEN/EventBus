package com.yzw;

/**
 * Created by yzw on 2016/4/9 0009.
 */
public class SubscribeMethod {
    String tag;
    int priority;
    String methodName;
    ThreadMode threadMode;

    public SubscribeMethod(String tag, int priority, String methodName, ThreadMode threadMode) {
        this.tag = tag;
        this.priority = priority;
        this.methodName = methodName;
        this.threadMode = threadMode;
    }
}
