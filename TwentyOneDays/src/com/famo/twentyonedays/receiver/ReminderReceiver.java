package com.famo.twentyonedays.receiver;

import org.apache.log4j.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.famo.twentyonedays.services.PushService;
import com.famo.twentyonedays.services.ReminderService;
import com.famo.twentyonedays.utils.AlarmAlertWakeLock;

public class ReminderReceiver extends BroadcastReceiver {

	private static final String TAG = "ReminderReceiver";
    private Logger logger=Logger.getLogger(ReminderReceiver.class);
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action!=null&&action.equals(ReminderService.ACTION_REMINDER)) {
			Log.d(TAG, "时间到了");
			logger.info("时间到了");
			intent.setClass(context, PushService.class);
			context.startService(intent);
		}
		
		AlarmAlertWakeLock.acquireCpuWakeLock(context);
	}

}
