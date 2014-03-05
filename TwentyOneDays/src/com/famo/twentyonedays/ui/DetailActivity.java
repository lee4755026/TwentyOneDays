package com.famo.twentyonedays.ui;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.ui.widget.SlideViewWidget;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DetailActivity extends Activity {

	private static final String TAG = "DetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		long planId=getIntent().getLongExtra(MainActivity.PLAN_ID, 0);
		
		String planName=getIntent().getStringExtra(MainActivity.PLAN_TITLE);
		Log.d(TAG, "planId="+planId+",planName="+planName);	
		
	}
	
}
