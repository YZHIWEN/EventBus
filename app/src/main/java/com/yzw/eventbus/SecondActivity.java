package com.yzw.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yzw.EventBundle;
import com.yzw.EventBus;
import com.yzw.Subscribe;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBundle.obtain().post("MainAcS");
            }
        });
    }

    @Subscribe(tag = "MainAcS", priority = 12)
    protected void fsjei(EventBundle bundle) {
        Log.e("Second Ac", "tag : Second Ac ---- fsjei");
        Toast.makeText(this, "Second Ac tag: MainAcS", Toast.LENGTH_SHORT).show();
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
