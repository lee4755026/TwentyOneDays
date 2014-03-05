package com.famo.twentyonedays.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.famo.twentyonedays.R;

public class DetailActivity extends Activity {

	private static final String TAG = "DetailActivity";
	private TextView title;
	private TextView content;
	private Button back;
	private View calendarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		findViews();
		bindEvents();
		bindData();
	}

	private void findViews() {
		title = (TextView) findViewById(android.R.id.title);
		content = (TextView) findViewById(R.id.content);
		back = (Button) findViewById(R.id.back);
		calendarView = findViewById(R.id.calendar);
	}

	private void bindEvents() {
		
	}

	private void bindData() {
		long planId = getIntent().getLongExtra(MainActivity.PLAN_ID, 0);
		String planName = getIntent().getStringExtra(MainActivity.PLAN_TITLE);
		Log.d(TAG, "planId=" + planId + ",planName=" + planName);
		title.setText(planName);
	}
	
	public void onViewClick(View v){
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;

		default:
			break;
		}
	}

}
