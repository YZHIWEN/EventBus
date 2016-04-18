package com.yzw.eventbus;

import android.os.Looper;
import android.text.BoringLayout;
import android.util.Log;
import android.view.TouchDelegate;

import com.yzw.EventBundle;
import com.yzw.SubscribeClass;
import com.yzw.Subscriber;
import com.yzw.Subscription;
import com.yzw.ThreadMode;
import com.yzw.util.EventBusLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yzw on 2016/4/8 0008.
 */
public class EventBus {

    final String TAG = "EVENT_BUS";
    private static EventBus bus;

    private UIScheduler uiScheduler;
    private IOScheduler ioScheduler;
    private EventQueue eventqueue;

    private Map<Object, List<String>> tagsBySubscriber;
    private Map<String, List<Subscription>> subscriptionMapbyTag;

    private EventBus(EventBusBuilder busBuilder) {
        tagsBySubscriber = new ConcurrentHashMap<>();
        subscriptionMapbyTag = new ConcurrentHashMap<>();
        this.eventqueue = EventQueue.getInstance();
        this.uiScheduler = UIScheduler.getInstance();
        this.ioScheduler = IOScheduler.getInstance();
        this.ioScheduler.setExecutorService(busBuilder.executorService);
    }

    public static synchronized EventBus getEvenBus() {
        if (bus == null) {
            bus = new EventBus(new EventBusBuilder());
        }
        return bus;
    }

    public static synchronized EventBus getEventBus(EventBusBuilder busBuilder) {
        if (bus == null) {
            bus = new EventBus(busBuilder);
        }
        return bus;
    }

    public void register(Object target) {
        // not getSimpleName
        String k = target.getClass().getName();
        if (tagsBySubscriber.containsKey(target)) {
            EventBusLog.i("register " + k, "has register");
            return;
        }

        try {
            EventBusLog.i("register", "create " + k + SubscribeClass.CLASS_SUFFIX);
            Class<?> targetProxyClass = Class.forName(k + SubscribeClass.CLASS_SUFFIX);
            Subscriber<Object> instance = (Subscriber<Object>) targetProxyClass.newInstance();

            instance.setTargetSubscriber(target);

            tagsBySubscriber.put(target, instance.getTags());
            List<String> tags = instance.getTags();
            for (String tag : tags) {
                addSubscribersMapbyTag(tag, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventBusLog.i("register fail", "create ClassNotFoundException");
        }
    }

    private void addSubscribersMapbyTag(String tag, Subscriber<Object> subscriber) {
        List<Subscription> subscrptionList = subscriptionMapbyTag.get(tag);
        List<Subscription> subscriberTagSubscription = subscriber.getTagSubscription(tag);
        if (subscriberTagSubscription == null || subscriberTagSubscription.size() == 0)
            return;

        if (subscrptionList == null) {
            subscrptionList = new ArrayList<>();
            subscrptionList.addAll(subscriberTagSubscription);
            subscriptionMapbyTag.put(tag, subscrptionList);
        } else {
            int size = subscrptionList.size();
            for (int i = 0; i < size; i++) {
                if (subscriberTagSubscription.size() == 0)
                    return;

                if (subscriberTagSubscription.get(0).priority > subscrptionList.get(i).priority) {
                    subscrptionList.add(i, subscriberTagSubscription.get(0));
                    size++;
                    subscriberTagSubscription.remove(0);
                }
            }
            if (subscrptionList.size() != 0)
                subscrptionList.addAll(subscriberTagSubscription);
        }
    }

    public void unRegister(Object target) {
        String k = target.getClass().getSimpleName();
        EventBusLog.i("unregister", k);
        List<String> tags = tagsBySubscriber.remove(target);

        if (tags == null)
            return;

        for (String tag : tags) {
            List<Subscription> subscriberList = subscriptionMapbyTag.get(tag);
            if (subscriberList == null)
                return;

            int size = subscriberList.size();
            for (int i = 0; i < size; i++) {
                if (subscriberList.get(i).getClass().getSimpleName().startsWith(target.getClass().getSimpleName())) {
                    subscriberList.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }

    // TODO: 2016/4/16 0016 how to release EventBundle
    public void post(String tag, EventBundle bundle) {
        List<Subscription> subscriberList = null;
        Runnable run = null;
        boolean isUIThread = Looper.myLooper() == Looper.getMainLooper();
        eventqueue.add(tag, bundle);

        if (!eventqueue.isPosting.get()) {
            eventqueue.isPosting.set(true);
            while (!eventqueue.isEmpty()) {
                EventQueue.Event event = eventqueue.get();
                subscriberList = subscriptionMapbyTag.get(event.tag);
                if (subscriberList == null)
                    return;
                for (Subscription subscrption : subscriberList) {
                    run = subscrption.subscriber.onReceive(tag, subscrption.priority, bundle);
                    if (run == null)
                        return;

                    if (subscrption.threadMode == ThreadMode.UI) {
                        if (isUIThread) {
                            run.run();
                        } else {
                            uiScheduler.enqueue(run);
                        }
                    } else if (subscrption.threadMode == ThreadMode.IO) {
                        ioScheduler.enqueue(run);
                    }
                }
            }
            eventqueue.isPosting.set(false);
        }
    }
}
