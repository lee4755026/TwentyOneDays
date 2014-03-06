package com.famo.twentyonedays.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.famo.twentyonedays.R;

public class AdditionActivity extends Activity {

	private static final String TAG = "AdditionActivity";
	private TextView title;
	private TextView content;
	private Button back;
	private View calendarView;
	private TextView planDate;
	private TextView planAlarmTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addition);

		findViews();
		bindEvents();
		bindData();
	}

	private void findViews() {
		title = (TextView) findViewById(android.R.id.title);
		content = (TextView) findViewById(R.id.content);
		back = (Button) findViewById(R.id.back);
		calendarView = findViewById(R.id.calendar);
		planDate=(TextView) findViewById(R.id.plan_performance_date);
		planAlarmTime=(TextView) findViewById(R.id.plan_alarm_time);
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
		case R.id.plan_performance_date:
			Log.d(TAG, "选择日期");
			startActivityForResult(new Intent(Intent.ACTION_PICK).setDataAndType(null, CalendarActivity.MIME_TYPE), 100);
			break;
		case R.id.plan_alarm_time:
			Log.d(TAG, "选择提醒时间");
			break;
		case R.id.save:
			Log.d(TAG, "保存");
			break;
		default:
			break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK) {
			int year = data.getIntExtra("year", 0);
			int month = data.getIntExtra("month", 0);
			int day = data.getIntExtra("day", 0);
			final Calendar dat = Calendar.getInstance();
			dat.set(Calendar.YEAR, year);
			dat.set(Calendar.MONTH, month);
			dat.set(Calendar.DAY_OF_MONTH, day);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd");
			Toast.makeText(AdditionActivity.this, format.format(dat.getTime()), Toast.LENGTH_LONG).show();
					
		}
	}

}
