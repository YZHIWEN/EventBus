package com.yzw.eventbus;

import com.yzw.EventBundle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yzw on 2016/4/15 0015.
 */
public class EventQueue {
    private List<Event> eventList;
    private static EventQueue queue;
    public volatile AtomicBoolean isPosting;

    private EventQueue() {
        this.eventList = Collections.synchronizedList(new LinkedList<Event>());
        this.isPosting = new AtomicBoolean(false);
    }

    public static synchronized EventQueue getInstance() {
        if (queue == null)
            queue = new EventQueue();
        return queue;
    }

    public boolean isEmpty() {
        return eventList.isEmpty();
    }

    public Event get() {
        return eventList.remove(0);
    }

    public void add(String tag, EventBundle bundle) {
        if (tag != null)
            this.eventList.add(new Event(tag, bundle));
    }

    public static class Event {
        String tag;
        EventBundle bundle;

        public Event(String tag, EventBundle bundle) {
            this.tag = tag;
            this.bundle = bundle;
        }
    }

}
