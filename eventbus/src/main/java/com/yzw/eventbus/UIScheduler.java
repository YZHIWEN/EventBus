package com.yzw.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by yzw on 2016/4/12 0012.
 */
public class UIScheduler extends Handler {

    private static class UISchedulerBuilder {
        public static UIScheduler uiScheduler = new UIScheduler();
    }

    public static UIScheduler getInstance() {
        return UISchedulerBuilder.uiScheduler;
    }

    private UIScheduler() {
        super(Looper.getMainLooper());
    }


    public void enqueue(Runnable runnable) {
        this.post(runnable);
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
