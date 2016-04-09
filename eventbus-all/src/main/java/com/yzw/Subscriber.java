package com.yzw;

import java.util.List;

/**
 * Created by yzw on 2016/4/7 0007.
 */
public interface Subscriber<T> {

    void onReceive(String tag, EventBundle bundle);

    List<String> getTags();

    List<Subscription> getTagSubscription(String tag);

    void setTargetSubscriber(T target);
}
