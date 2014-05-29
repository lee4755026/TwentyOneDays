package com.famo.twentyonedays.receiver;

import com.famo.twentyonedays.services.ReminderService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PersistReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ReminderService.class));
    }

}
