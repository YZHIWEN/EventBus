package com.yzw.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.yzw.EventBundle;
import com.yzw.EventBus;
import com.yzw.Subscribe;
import com.yzw.ThreadMode;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getEvenBus().post("MainAcS", null);
            }
        });

        findViewById(R.id.bottom_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getEvenBus().register(this);
    }

    @Subscribe(tag = "MainAcS", threadmode = ThreadMode.UI, priority = 22)
    public void s(EventBundle bundle) {
        Log.e("MainAc", "tag : MainAcS ---- s");
        Toast.makeText(this, "tag mainac", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(tag = "MainAcSF", threadmode = ThreadMode.IO, priority = 12)
    public void sf(EventBundle bundle) {
    }


    @Subscribe(tag = "MainAcS", threadmode = ThreadMode.UI, priority = 2)
    public void sfejios(EventBundle bundle) {
        Log.e("MainAc", "tag : MainAcS ---- sfejios");
        Toast.makeText(this, "tag mainac", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        EventBus.getEvenBus().unRegister(this);
    }
}
