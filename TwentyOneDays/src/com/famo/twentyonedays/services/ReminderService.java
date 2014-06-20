package com.famo.twentyonedays.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

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
	private Logger logger;

	@Override
	public void onCreate() {
		super.onCreate();
		
		logger=Logger.getLogger(ReminderService.class);
		Log.d(TAG, "服务启动了");

		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		List<PlanEntry> entries = new DataBaseManager(this).getUnderwayPlanEntries();
		for (PlanEntry entry : entries) {
		    Log.d(TAG, entry.title);
			startNewAlarm(entry);
		}
	}
	

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	    return START_STICKY;
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
//			calendar.add(Calendar.DAY_OF_MONTH, 1);
		    calendar.setTimeInMillis(System.currentTimeMillis());
		    calendar.add(Calendar.DAY_OF_MONTH, 1);//1天后触发
		}
		long firstTime = calendar.getTimeInMillis();
//		firstTime=System.currentTimeMillis()+5*1000;
//		Log.d(TAG, "firstTime="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(firstTime)));
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP,firstTime, 24*60*60*1000, sender);//间隔1天
		 
//		Log.d(TAG, "定时开始");
		logger.info("定时开始");
		
	}
	private void stopAlarm(PlanEntry entry){
		Intent intent = new Intent(ACTION_REMINDER);
		int uniqueRequestCode=entry.id;
		PendingIntent senderToEnd = PendingIntent.getBroadcast(this, uniqueRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(senderToEnd);
	}

	@Override
	public void onDestroy() {
	    Log.d(TAG, "onDestroy()");
		super.onDestroy();
		List<PlanEntry> entries = new DataBaseManager(this).getPlanEntries();
		for (PlanEntry entry : entries) {
			stopAlarm(entry);
		}
		
		startService(new Intent(this, ReminderService.class));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
