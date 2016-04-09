package com.yzw.util;

/**
 * Created by yzw on 2016/4/8 0008.
 */
public class EventBusLog {
    public static boolean DEBUS = true;

    public static void i(String tag, String info) {
        if (DEBUS)
            System.out.println("EventBus Log : " + tag + "  Info : " + info);
    }

}
