package com.famo.twentyonedays.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.famo.twentyonedays.services.PushService;
import com.famo.twentyonedays.services.ReminderService;

public class ReminderReceiver extends BroadcastReceiver {

	private static final String TAG = "ReminderReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		String action = intent.getAction();
		if (action!=null&&action.equals(ReminderService.ACTION_REMINDER)) {
			Log.d(TAG, "时间到了");
			intent.setClass(context, PushService.class);
			context.startService(intent);
		}
	}

}
