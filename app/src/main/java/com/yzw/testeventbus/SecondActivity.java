package com.yzw.testeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yzw.EventBundle;
import com.yzw.Subscribe;
import com.yzw.ThreadMode;
import com.yzw.eventbus.EventBus;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EventBundle.obtain().post("MainAcS");
                EventBundle e = new EventBundle();
                EventBus.getEvenBus().post("MainAcS", null);
            }
        });
    }

    @Subscribe(tag = "MainAcS", threadmode = ThreadMode.IO, priority = 12)
    public void fsjei(EventBundle bundle) {
        Log.e("Second Ac", "tag : Second Ac ---- fsjei");
        Toast.makeText(this, "Second Ac tag: MainAcS", Toast.LENGTH_SHORT).show();
        throw new NullPointerException("furk ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getEvenBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getEvenBus().register(this);
    }
}
