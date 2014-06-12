package com.famo.twentyonedays.ui;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.famo.twentyonedays.MyApplication;

import android.app.Activity;
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
        
        getMyApplication().getActivities().add(this);
    }
    
    protected MyApplication getMyApplication() {
        return (MyApplication) getApplication();
    }
    
    
    
}
