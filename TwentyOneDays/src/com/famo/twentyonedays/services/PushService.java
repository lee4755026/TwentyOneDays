package com.famo.twentyonedays.services;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.ui.DetailActivity;
import com.famo.twentyonedays.ui.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public class PushService extends Service {
	private int planId;
	private String planTitle;
	private String planContent;
	

	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	


	@Override
    public void onStart(Intent intent, int startId) {
	    super.onStart(intent, startId);
	    if(intent==null){
	        return;
        }
        planId=intent.getIntExtra(MainActivity.PLAN_ID, 0);
        planTitle=intent.getStringExtra(MainActivity.PLAN_TITLE);
        planContent=intent.getStringExtra(MainActivity.PLAN_CONTENT);
        showNotification();
    }




    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}



	private void showNotification() {
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent send=new Intent(this,DetailActivity.class);
		send.putExtra(MainActivity.PLAN_ID, planId);
		send.putExtra(MainActivity.PLAN_TITLE,planTitle);
		send.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent=PendingIntent.getActivity(this, 0, send, 0);
		Notification notification=new Notification(R.drawable.ic_launcher,planTitle,System.currentTimeMillis());
		notification.setLatestEventInfo(this, planTitle, planContent, contentIntent);
		
		nm.notify(R.string.app_name, notification);
		
		Thread th=new Thread(mTask);
		th.start();
	}
	
	
	
	@Override
	public void onDestroy() {
		nm.cancel(R.string.app_name);
		super.onDestroy();
	}



	Runnable mTask=new Runnable() {
		
		@Override
		public void run() {
			long endTime=System.currentTimeMillis()+15*1000;
			while (System.currentTimeMillis()<endTime) {
				synchronized(mBinder){
					try {
						mBinder.wait(endTime-System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
				
			}
			PushService.this.stopSelf();
		}
	};
	private NotificationManager nm;

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
