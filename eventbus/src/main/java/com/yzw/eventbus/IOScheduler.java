package com.yzw.eventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by yzw on 2016/4/12 0012.
 */
public class IOScheduler {

    private ExecutorService  executorService;

    private static IOScheduler ioScheduler;

    public static IOScheduler getInstance() {
        if (ioScheduler == null) {
            ioScheduler = new IOScheduler();
        }
        return ioScheduler;
    }

    public void setExecutorService(ExecutorService  executorService) {
        this.executorService = executorService;
    }

    public void enqueue(Runnable runnable) {
        if (this.executorService != null)
            this.executorService.execute(runnable);
    }


}
