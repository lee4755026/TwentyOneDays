package com.famo.twentyonedays.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.famo.twentyonedays.R;
import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;

public class AdditionActivity extends Activity {

	private static final String TAG = "AdditionActivity";
	private TextView TvTitle;
	private TextView TvContent;
	private Button back;
	private View calendarView;
	private TextView planDate;
	private TextView planAlarmTime;
	
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addition);

		findViews();
		bindEvents();
		bindData();
	}

	private void findViews() {
		TvTitle = (TextView) findViewById(android.R.id.title);
		TvContent = (TextView) findViewById(R.id.content);
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
		TvTitle.setText(planName);
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
			PlanEntry entry=new PlanEntry();
			entry.title=TvTitle.getText().toString();
			entry.content=TvContent.getText().toString();
			Calendar calendar=Calendar.getInstance();
			entry.createTime=sdf.format(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 2);
			entry.startDate=sdf.format(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 20);
			entry.endDate=sdf.format(calendar.getTime());
			entry.reminderTime="21:00";
			entry.color=Color.RED;
			
			DataBaseManager manager=new DataBaseManager(this);
			manager.insert(entry);
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
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String start=sdf.format(dat.getTime());
			dat.add(Calendar.DAY_OF_MONTH, 20);
			String end=sdf.format(dat.getTime());
			planDate.setText(start+"--"+end);
					
		}
	}

}
