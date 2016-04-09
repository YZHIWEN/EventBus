package com.yzw;

import com.yzw.util.EventBusLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yzw on 2016/4/8 0008.
 */
public class EventBus {

    private static EventBus bus;

    // 订阅者：订阅事件类型
    private Map<Object, List<String>> tagsBySubscriber;
    // Tag代理对象的集合，需要按照优先级排序
    private Map<String, List<Subscription>> subscriptionMapbyTag;

//    private Map<Class, Object> targetMap;
//    // 对象的代理类集合；K：注册EventBus对象String，V:K的代理对象
//    private Map<String, Subscriber<Object>> targetProxyMap;

    private EventBus(EventBusBuilder busBuilder) {
        tagsBySubscriber = new ConcurrentHashMap<>();
        subscriptionMapbyTag = new ConcurrentHashMap<>();
    }

    public static synchronized EventBus getEvenBus() {
        if (bus == null) {
            bus = new EventBus(new EventBusBuilder());
        }
        return bus;
    }

    public void register(Object target) {
        // not getSimpleName
        String k = target.getClass().getName();
        if (tagsBySubscriber.containsKey(target)) {
            com.yzw.util.EventBusLog.i("register " + k, "has register");
            return;
        }

        try {
            com.yzw.util.EventBusLog.i("register", "create " + k + SubscribeClass.CLASS_SUFFIX);
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
            com.yzw.util.EventBusLog.i("register fail", "create ClassNotFoundException");
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
        com.yzw.util.EventBusLog.i("unregister", k);
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

    public void post(String tag, EventBundle bundle) {
        List<Subscription> subscriberList = subscriptionMapbyTag.get(tag);
        if (subscriberList == null)
            return;
        for (Subscription subscrption : subscriberList) {
            subscrption.subscriber.onReceive(tag, bundle);
        }
    }

}
