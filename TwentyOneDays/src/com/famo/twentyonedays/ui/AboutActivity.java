package com.famo.twentyonedays.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.famo.twentyonedays.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(R.string.about);
        
    }

}
