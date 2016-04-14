package com.yzw;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzw on 2016/4/7 0007.
 */
public final class EventBundle {

    private EventBundle next;

    // 怎么使用ArrayMap
    private Map<String, Object> bundlemaps;

    public EventBundle() {
        this.bundlemaps = new HashMap<String, Object>();
    }

//    public void post(String tag) {
//        EventBus.getEvenBus().post(tag, this);
//    }

    // TODO: 2016/4/10 0010 待关联EventQueue 
    public static EventBundle obtain() {
        return new EventBundle();
    }
}
