package com.yzw;

/**
 * Created by yzw on 2016/4/7 0007.
 */
public interface Subscriber {
    void onReceive(String tag, EventBundle bundle);
}
