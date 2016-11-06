package com.daotangbill.rick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private SplashView splashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashView = new SplashView(this);
        RelativeLayout layout = (RelativeLayout) getRootView(this);
        layout.addView(splashView);
        startSplashDataLoad();
    }

    Handler handler = new Handler();

    private void startSplashDataLoad() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //模拟 数据加载 完毕-》开启后面的动画
                splashView.SplashDisapper();
            }
        }, 3000);
    }

    private static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }
}
