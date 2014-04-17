package com.famo.twentyonedays.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.receiver.ReminderReceiver;
import com.famo.twentyonedays.ui.MainActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * 定时提醒服务
 * 
 * @author LiChaofei <br/>
 *         2014-3-13 上午10:49:55
 */
public class ReminderService extends Service {

	public static final String ACTION_REMINDER = "com.famo.action.reminder";
	private static final String TAG = "ReminderService";
	private String reminderTime = "15:51";
	private AlarmManager am;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "服务启动了");

		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		List<PlanEntry> entries = new DataBaseManager(this).getPlanEntries();
		for (PlanEntry entry : entries) {
			startNewAlarm(entry);
		}

	}

	private void startNewAlarm(PlanEntry entry) {
		Log.d(TAG, entry.toString());
		// Intent intent=new Intent(ReminderService.this,ReminderReceiver.class);
		Intent intent = new Intent(ACTION_REMINDER);
		intent.putExtra(MainActivity.PLAN_ID, entry.id);
		intent.putExtra(MainActivity.PLAN_TITLE, entry.title);
		intent.putExtra(MainActivity.PLAN_CONTENT, entry.content);
		reminderTime = entry.reminderTime;
		int uniqueRequestCode=entry.id;
		PendingIntent sender = PendingIntent.getBroadcast(this, uniqueRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY,
				Integer.valueOf(reminderTime.split(":")[0]));
		calendar.set(Calendar.MINUTE,
				Integer.valueOf(reminderTime.split(":")[1]));
		calendar.set(Calendar.SECOND, 0);
		
		if(calendar.getTimeInMillis()<System.currentTimeMillis()){
			calendar.add(Calendar.MILLISECOND, (int) (System.currentTimeMillis()-calendar.getTimeInMillis()+60*1000));
		}
		long firstTime = calendar.getTimeInMillis();
		Log.d(TAG, "firstTime="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(firstTime)));
		// Schedule the alarm !
//		am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, 15 * 1000, sender);
		
		
		
		firstTime = SystemClock.elapsedRealtime();
		firstTime += 10*1000;
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		 am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		                            firstTime, 5*1000, sender);
		
		Log.d(TAG, "定时开始");
	}
	private void stopAlarm(PlanEntry entry){
		Intent intent = new Intent(ACTION_REMINDER);
//		intent.putExtra(MainActivity.PLAN_ID, entry.id);
//		intent.putExtra(MainActivity.PLAN_TITLE, entry.title);
//		intent.putExtra(MainActivity.PLAN_CONTENT, entry.content);
//		reminderTime = entry.reminderTime;
		int uniqueRequestCode=entry.id;
		PendingIntent senderToEnd = PendingIntent.getBroadcast(this, uniqueRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(senderToEnd);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		List<PlanEntry> entries = new DataBaseManager(this).getPlanEntries();
		for (PlanEntry entry : entries) {
			stopAlarm(entry);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
