package com.famo.twentyonedays.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.ui.widget.calender.CalendarView;

public class DetailActivity extends Activity {

	private static final String TAG = "DetailActivity";
	private TextView title;
	private TextView content;
	private TextView alarmTime;
	private CalendarView calendarView;
	
	private PlanEntry entry;
	private int planId;
	private String planName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		findViews();
		
		planId = getIntent().getIntExtra(MainActivity.PLAN_ID, 0);
		planName = getIntent().getStringExtra(MainActivity.PLAN_TITLE);
		Log.d(TAG, "planId=" + planId + ",planName=" + planName);
		new LoadPlanTask().execute();
	}

	private void findViews() {
		title = (TextView) findViewById(android.R.id.title);
		content = (TextView) findViewById(R.id.content);
		alarmTime=(TextView) findViewById(R.id.plan_alarm_time);
		calendarView = (CalendarView) findViewById(R.id.calendar);
	}


	private void bindData() {
		Log.d(TAG, entry.toString());
		title.setText(planName);
		content.setText(entry.content);
		alarmTime.setText(entry.reminderTime);
		calendarView.setRange(entry.startDate,entry.endDate);
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
	
	private class LoadPlanTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			DataBaseManager manager=new DataBaseManager(DetailActivity.this);
			entry=manager.getDataById(planId);
			return entry!=null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				bindData();
			}else{
				Toast.makeText(DetailActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		
		
	}

}
