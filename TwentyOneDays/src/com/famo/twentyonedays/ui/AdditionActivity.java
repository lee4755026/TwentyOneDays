package com.famo.twentyonedays.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.utils.Tools;

public class AdditionActivity extends Activity {

	private static final String TAG = "AdditionActivity";
	private TextView TvTitle;
	private TextView TvContent;
	private Button back;
	private View calendarView;
	private TextView planDate;
	private TextView planAlarmTime;
	// Time changed flag
	private boolean timeChanged = false;
	
	// Time scrolled flag
	private boolean timeScrolled = false;
	
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
		Calendar calendar=Calendar.getInstance();
		String startDate=sdf.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 20);
		String endDate=sdf.format(calendar.getTime());
		planDate.setText(startDate+"--"+endDate);
		planAlarmTime.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
	}
	
	public void onViewClick(View v){
		switch (v.getId()) {
		case R.id.back:
			onBackPressed();
			break;
		case R.id.plan_performance_date:
			Log.d(TAG, "选择日期");
//			startActivityForResult(new Intent(Intent.ACTION_PICK).setDataAndType(null, CalendarActivity.MIME_TYPE), 100);
			showDatePicker();
			break;
		case R.id.plan_alarm_time:
			Log.d(TAG, "选择提醒时间");
			showTimePicker();
			break;
		case R.id.save:
			Log.d(TAG, "保存");
			if(!validate()){
				break;
			}
			PlanEntry entry = new PlanEntry();
			entry.title = TvTitle.getText().toString();
			entry.content = TvContent.getText().toString();

			String[] strs = planDate.getText().toString().split("--");
			entry.startDate = strs[0];
			entry.endDate = strs[1];
			entry.reminderTime = planAlarmTime.getText().toString();
			entry.color = Color.RED;
			entry.createTime=Tools.currentSystemTime();

			DataBaseManager manager = new DataBaseManager(
					AdditionActivity.this);
			if (manager.insert(entry)) {
				Log.d(TAG, "保存成功！");
				Toast.makeText(AdditionActivity.this,
						R.string.save_success,
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 校验
	 * @author LiChaofei
	 * <br/>2014-3-10 下午4:19:41
	 * @return TODO
	 */
	private boolean validate() {
		if(TextUtils.isEmpty(TvTitle.getText())){
			TvTitle.setError("起个名字吧     ");
			return false;
		}
		if(TextUtils.isEmpty(TvContent.getText())){
			TvContent.setError("写点什么吧     ");
			return false;

		}
		return true;
		
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
	private void showDatePicker(){
		View view=getLayoutInflater().inflate(R.layout.date_layout, null);
		 Calendar calendar = Calendar.getInstance();

	        final WheelView month = (WheelView) view.findViewById(R.id.month);
	        final WheelView year = (WheelView) view.findViewById(R.id.year);
	        final WheelView day = (WheelView) view.findViewById(R.id.day);
	        
	        OnWheelChangedListener listener = new OnWheelChangedListener() {
	            public void onChanged(WheelView wheel, int oldValue, int newValue) {
	                updateDays(year, month, day);
	            }
	        };

	        // month
	        int curMonth = calendar.get(Calendar.MONTH);
	        String months[] = new String[] {"一月", "二月", "三月", "四月", "五月",
	                "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
	        month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
	        month.setCurrentItem(curMonth);
	        month.addChangingListener(listener);
	        month.setCyclic(true);
	    
	        // year
	        int curYear = calendar.get(Calendar.YEAR);
	        year.setViewAdapter(new DateNumericAdapter(this, curYear, curYear + 10, 0));
	        year.setCurrentItem(curYear);
	        year.addChangingListener(listener);
	        
	        //day
	        updateDays(year, month, day);
	        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
	        day.setCyclic(true);
	        
	        AlertDialog.Builder builder=new AlertDialog.Builder(AdditionActivity.this)
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int yearValue = Calendar.getInstance().get(Calendar.YEAR)+year.getCurrentItem();
					int monthValue = month.getCurrentItem();
					int dayValue = day.getCurrentItem()+1;
					final Calendar dat = Calendar.getInstance();
					dat.set(Calendar.YEAR, yearValue);
					dat.set(Calendar.MONTH, monthValue);
					dat.set(Calendar.DAY_OF_MONTH, dayValue);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					String start=sdf.format(dat.getTime());
					dat.add(Calendar.DAY_OF_MONTH, 20);
					String end=sdf.format(dat.getTime());
					planDate.setText(start+"--"+end);
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			builder.create().show();
	    }
	    
	    /**
	     * Updates day wheel. Sets max days according to selected month and year
	     */
	    void updateDays(WheelView year, WheelView month, WheelView day) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
	        calendar.set(Calendar.MONTH, month.getCurrentItem());
	        
	        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
	        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
	        day.setCurrentItem(curDay - 1, true);
	    }
	    
	    
	private void showTimePicker(){
		View view=getLayoutInflater().inflate(R.layout.time_layout, null);
		
		final WheelView hours = (WheelView) view.findViewById(R.id.hour);
		hours.setViewAdapter(new NumericWheelAdapter(this, 0, 23));
		hours.setCyclic(true);
	
		final WheelView mins = (WheelView) view.findViewById(R.id.mins);
		mins.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
		mins.setCyclic(true);
	
		// set current time
		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);
	
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);
	
		// add listeners
		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");
	
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					timeChanged = true;
//					picker.setCurrentHour(hours.getCurrentItem());
//					picker.setCurrentMinute(mins.getCurrentItem());
					timeChanged = false;
				}
			}
		};
		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);
		
		OnWheelClickedListener click = new OnWheelClickedListener() {
            public void onItemClicked(WheelView wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        hours.addClickingListener(click);
        mins.addClickingListener(click);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;
			}
			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				timeChanged = true;
//				picker.setCurrentHour(hours.getCurrentItem());
//				picker.setCurrentMinute(mins.getCurrentItem());
				timeChanged = false;
			}
		};
		
		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(AdditionActivity.this)
		.setView(view)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				Toast.makeText(AdditionActivity.this, hours.getCurrentItem()+","+mins.getCurrentItem(), Toast.LENGTH_SHORT).show();
				planAlarmTime.setText(String.format("%02d:%02d", hours.getCurrentItem(),mins.getCurrentItem()));
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.create().show();
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * @param wheel the wheel
	 * @param label the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				//wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}
	
	/**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
//            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
    
    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
//            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

}
