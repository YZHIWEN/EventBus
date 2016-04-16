package com.yzw;

import com.yzw.util.EventBusLog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yzw on 2016/4/7 0007.
 */
public final class EventBundle {

    private static final String TAG = EventBundle.class.getSimpleName();
    private EventBundle next;

    // 怎么使用ArrayMap
    private Map<String, Object> bundlemaps;

    public EventBundle() {
        this.bundlemaps = new ConcurrentHashMap<>();
    }

//    public void post(String tag) {
//        EventBus.getEvenBus().post(tag, this);
//    }

    // TODO: 2016/4/10 0010 待关联EventQueue 
    public static EventBundle obtain() {
        return new EventBundle();
    }


    public void putChar(String key, char c) {
        bundlemaps.put(key, c);
    }

    public char getChar(String key, char defaultvale) {
        try {
            char c = (char) bundlemaps.get(key);
            return c;
        } catch (ClassCastException e) {
            EventBusLog.i(TAG, "getChar ClassCastException key: " + key);
            return defaultvale;
        }
    }

    public void putInt(String key, int i) {
        bundlemaps.put(key, i);
    }

    public int getInt(String key, int defaultvale) {
        try {
            return (int) bundlemaps.get(key);
        } catch (ClassCastException e) {
            EventBusLog.i(TAG, "getInt ClassCastException key: " + key);
            return defaultvale;
        }
    }


    public void putDouble(String key, double i) {
        bundlemaps.put(key, i);
    }

    public double getDouble(String key, double defaultvale) {
        try {
            return (int) bundlemaps.get(key);
        } catch (ClassCastException e) {
            EventBusLog.i(TAG, "getInt ClassCastException key: " + key);
            return defaultvale;
        }
    }

    public void putString(String key, String i) {
        bundlemaps.put(key, i);
    }

    public String getString(String key, String defaultvale) {
        try {
            return (String) bundlemaps.get(key);
        } catch (ClassCastException e) {
            EventBusLog.i(TAG, "getInt ClassCastException key: " + key);
            return defaultvale;
        }
    }

    public void putObject(String key, Object i) {
        bundlemaps.put(key, i);
    }

    public Object getObject(String key, Object defaultvale) {
        try {
            return (String) bundlemaps.get(key);
        } catch (ClassCastException e) {
            EventBusLog.i(TAG, "getInt ClassCastException key: " + key);
            return defaultvale;
        }
    }

    public void putSerializable(String key, Serializable i) {
        bundlemaps.put(key, i);
    }

    public Serializable getObject(String key, Serializable defaultvale) {
        try {
            return (Serializable) bundlemaps.get(key);
        } catch (ClassCastException e) {
            EventBusLog.i(TAG, "getInt ClassCastException key: " + key);
            return defaultvale;
        }
    }

    public void clear() {
        this.bundlemaps.clear();
    }

    @Override
    public String toString() {
        return "EventBundle{" +
                "bundlemaps=" + bundlemaps +
                '}';
    }
}
