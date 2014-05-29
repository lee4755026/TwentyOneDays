package com.famo.twentyonedays.services;

import org.apache.log4j.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.ui.DetailActivity;
import com.famo.twentyonedays.ui.MainActivity;

public class PushService extends Service {
	private int planId;
	private String planTitle;
	private String planContent;
	private NotificationManager nm;
	private Logger logger;
	@Override
	public void onCreate() {
		super.onCreate();
		logger=Logger.getLogger(PushService.class);
	}

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
        planId=intent.getIntExtra(MainActivity.PLAN_ID, 0);
        planTitle=intent.getStringExtra(MainActivity.PLAN_TITLE);
        planContent=intent.getStringExtra(MainActivity.PLAN_CONTENT);
        logger.info("planId="+planId+",planTitle="+planTitle+",planContent="+planContent);
        showNotification();
        }
		return super.onStartCommand(intent, flags, startId);
	}



	private void showNotification() {
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent send=new Intent(this,DetailActivity.class);
		send.putExtra(MainActivity.PLAN_ID, planId);
		send.putExtra(MainActivity.PLAN_TITLE,planTitle);
		send.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Notification notification=null;
		PendingIntent pendingIntent=null;
		
		pendingIntent=PendingIntent.getActivity(this, 0, send, 0);
//		notification=new Notification(R.drawable.ic_launcher,planTitle,System.currentTimeMillis());
//		notification.setLatestEventInfo(this, planTitle, planContent, pendingIntent);
		
//		int currentapiVersion=android.os.Build.VERSION.SDK_INT;
//		if(currentapiVersion>11) {
    		TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
    		stackBuilder.addParentStack(DetailActivity.class);
    		stackBuilder.addNextIntent(send);
    		pendingIntent=stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    		
    		notification=new NotificationCompat.Builder(this)
    		.setAutoCancel(true)
    //		.setContentInfo("contentinfo")
    		.setContentTitle(planTitle)
    		.setContentText(planContent)
    		.setDefaults(Notification.DEFAULT_SOUND)
    //		.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
    //		.setTicker("Nomal ")
    		.setSmallIcon(R.drawable.ic_launcher)
    		.setContentIntent(pendingIntent)
    		.build();
//		}
		
		nm.notify(planId, notification);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	/**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    private final IBinder mBinder = new Binder() {
        @Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
		        int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

}
