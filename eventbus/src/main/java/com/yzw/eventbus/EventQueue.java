package com.yzw.eventbus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yzw on 2016/4/15 0015.
 */
public class EventQueue {
    private List<Runnable> runnableList;
    private static EventQueue queue;
    public volatile AtomicBoolean isPosting;

    private EventQueue() {
        this.runnableList = Collections.synchronizedList(new LinkedList<Runnable>());
        this.isPosting = new AtomicBoolean(false);
    }

    public static synchronized EventQueue getInstance() {
        if (queue == null)
            queue = new EventQueue();
        return queue;
    }

    public boolean isEmpty() {
        return runnableList.isEmpty();
    }

    public Runnable get() {
        return runnableList.remove(0);
    }

    public void add(Runnable runnable) {
        if (runnable != null)
            this.runnableList.add(runnable);
    }

}
