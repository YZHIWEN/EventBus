package com.yzw.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by yzw on 2016/4/8 0008.
 */
public class EventBusBuilder {

    private final static ExecutorService  DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    boolean isLog = true;
    ExecutorService  executorService = DEFAULT_EXECUTOR_SERVICE;


    public EventBusBuilder ioScheduler(ExecutorService  executorService) {
        this.executorService = executorService;
        return this;
    }

    public EventBusBuilder isLog(boolean isLog) {
        this.isLog = isLog;
        return this;
    }

    public EventBus build() {
        return EventBus.getEvenBus();
    }


}
