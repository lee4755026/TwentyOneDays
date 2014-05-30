package com.famo.twentyonedays.ui;


import org.apache.log4j.Logger;

import com.famo.twentyonedays.MyApplication;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {
    protected Logger logger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        logger=Logger.getLogger(BaseActivity.class);
//        ActionBar actionBar=getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
    }
    
    protected MyApplication getMyApplication() {
        return (MyApplication) getApplication();
    }
    
}
