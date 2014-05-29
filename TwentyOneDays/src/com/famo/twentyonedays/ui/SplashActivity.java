package com.famo.twentyonedays.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.famo.twentyonedays.R;

public class SplashActivity extends Activity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        
        setContentView(R.layout.activity_splash);
        
        findViews();
        bindEvents();
    }

    private void findViews() {
        button=(Button) findViewById(android.R.id.button1);
        
    }

    private void bindEvents() {
       button.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
           Intent intent=new Intent(SplashActivity.this, MainActivity.class);
           startActivity(intent);
           finish();
        }
    });
        
    }

}
