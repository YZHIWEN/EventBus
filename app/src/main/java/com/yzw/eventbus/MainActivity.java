package com.yzw.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yzw.EventBundle;
import com.yzw.Subscribe;
import com.yzw.ThreadMode;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Subscribe(tag = "MainAcS", threadmode = ThreadMode.UI, priority = 22)
    public void s(EventBundle bundle) {
    }

    @Subscribe(tag = "MainAcSF", threadmode = ThreadMode.IO, priority = 12)
    public void sf(EventBundle bundle) {
    }


    @Subscribe(tag = "MainAcS", threadmode = ThreadMode.UI, priority = 2)
    public void sfejios(EventBundle bundle){}

}
