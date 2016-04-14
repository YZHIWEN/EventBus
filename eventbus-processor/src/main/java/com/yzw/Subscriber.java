package com.yzw;

import java.util.List;

/**
 * Created by yzw on 2016/4/7 0007.
 */
public interface Subscriber<T> {

    Runnable onReceive(String tag,int priority, EventBundle bundle);

    List<String> getTags();

    List<Subscription> getTagSubscription(String tag);

    void setTargetSubscriber(T target);
}
