package com.yzw.testeventbus;

import android.app.Application;
import android.media.AudioRecord;
import android.util.Log;

import com.yzw.EventBundle;
import com.yzw.Subscribe;
import com.yzw.ThreadMode;
import com.yzw.eventbus.EventBus;

/**
 * Created by yzw on 2016/4/16 0016.
 */
public class APP extends Application {

    private static final String TAG = APP.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getEvenBus().register(this);
    }

    @Subscribe(tag = "MainAcS",threadmode = ThreadMode.IO)
    public void appEvent(EventBundle bundle) {
        Log.e(TAG, "appEvent: thred "+ Thread.currentThread().toString() );
        Log.e(TAG, "appEvent: " + bundle.toString());
    }
}
