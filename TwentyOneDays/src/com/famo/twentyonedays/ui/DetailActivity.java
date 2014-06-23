package com.famo.twentyonedays.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.ui.widget.ProgressLayout;
import com.famo.twentyonedays.ui.widget.ProgressUnit;
import com.famo.twentyonedays.ui.widget.calender.CalendarView;
import com.famo.twentyonedays.utils.AccessTokenKeeper;
import com.famo.twentyonedays.utils.Constants;
import com.famo.twentyonedays.utils.MyDialog;
import com.famo.twentyonedays.utils.Tools;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

public class DetailActivity extends BaseActivity {

	public static final String CONTENT = "content";
    public static final String FILE_PATH = "filePath";
    private static final String TAG = "DetailActivity";
	private TextView title;
	private TextView content;
	private TextView alarmTime;
//	private CalendarView calendarView;
	private ProgressLayout progressLayout;
	
	private PlanEntry entry;
	private int planId;
	private String planName;
   
    private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		findViews();
		
		planId = getIntent().getIntExtra(MainActivity.PLAN_ID, 0);
		planName = getIntent().getStringExtra(MainActivity.PLAN_TITLE);
		Log.d(TAG, "planId=" + planId + ",planName=" + planName);
		new LoadPlanTask().execute();
//		DataBaseManager manager=new DataBaseManager(DetailActivity.this);
//		entry=manager.getDataById(planId);
//		bindData();
		
//		try {
//		    ViewConfiguration mconfig = ViewConfiguration.get(this);
//		           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
//		           if(menuKeyField != null) {
//		               menuKeyField.setAccessible(true);
//		               menuKeyField.setBoolean(mconfig, false);
//		           }
//		       } catch (Exception ex) {
//		       }

	}

	private void findViews() {
		title = (TextView) findViewById(android.R.id.title);
		content = (TextView) findViewById(R.id.content);
		alarmTime=(TextView) findViewById(R.id.plan_alarm_time);
//		calendarView = (CalendarView) findViewById(R.id.calendar);
		progressLayout=(ProgressLayout) findViewById(R.id.progress);
	}


	private void bindData() {
		Log.d(TAG, entry.toString());
		
		title.setText(planName);
		content.setText(entry.content);
		
		alarmTime.setText(entry.reminderTime);
//		calendarView.setRange(entry.startDate,entry.endDate);
		SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = null;
		Date endDate = null;
        try {
            startDate = format.parse(entry.startDate);
            endDate = format.parse(entry.endDate);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(startDate);
		for(int i=0;i<21;i++) {
		    ProgressUnit unit=new ProgressUnit(this);
		    
		    if(i>0) {
		        calendar.add(Calendar.DAY_OF_MONTH, 1);
		    }
		    unit.setPlanDate(calendar.getTime());

		    unit.setText(i+1+"");

		    progressLayout.addView(unit);
		}
		
	      try {
	          Date  date=new SimpleDateFormat("yyyy/MM/dd").parse(entry.endDate);
	          if(!date.after(new Date())) {//计划结束
	              Log.d(TAG, "计划结束");
	              MyDialog.Alert(DetailActivity.this, "这个计划已经完成了，但习惯的养成是一个长期的过程，在接下来的日子里，你要继续努力哦！").show();
	          }
	            
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
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
	
	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) { 
	      MenuInflater inflater=getMenuInflater();
	      inflater.inflate(R.menu.share_menu, menu);
//	     ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_share));
//	        // Initialize the share intent  
//	        Intent intent = new Intent(Intent.ACTION_SEND);  
//	        intent.setType("text/plain");  
//	        intent.putExtra(Intent.EXTRA_TEXT, "Text I want to share");  
//	        provider.setShareIntent(intent); 
	      return true;
//	        MenuItemCompat.setShowAsAction( 
//	                menu 
//	                .add("No.1") 
//	                .setIcon(android.R.drawable.ic_menu_share)
//	                .setTitle("微博"),
//	                MenuItemCompat.SHOW_AS_ACTION_IF_ROOM); 
//	        MenuItemCompat.setShowAsAction( 
//	                menu 
//	                .add("No.2") 
//	                .setIcon(android.R.drawable.ic_menu_compass),  
//	                MenuItemCompat.SHOW_AS_ACTION_IF_ROOM); 
//	        MenuItemCompat.setShowAsAction( 
//	                menu 
//	                .add("No.3") 
//	                .setIcon(android.R.drawable.ic_menu_more),  
//	                MenuItemCompat.SHOW_AS_ACTION_IF_ROOM); 
//	        return true; 
	    } 
	     
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) { 
	        switch(item.getItemId()) {
	        case R.id.menu_share:
//	            Toast.makeText(DetailActivity.this, "share content to sina", Toast.LENGTH_SHORT).show();
	            postContent();
	            break;
	        }
	        return super.onOptionsItemSelected(item); 
	    } 
	
	    private void postContent() {
	        int passed=0;
	        try {
	            Calendar calendarStart=Calendar.getInstance();
	            Calendar calendar=Calendar.getInstance();
	            Date dateStart=new SimpleDateFormat("yyyy/MM/dd").parse(entry.startDate);
	            calendarStart.setTime(dateStart);
	            long l=calendar.getTimeInMillis()-calendarStart.getTimeInMillis();
	            passed=new Long(l/(1000*60*60*24)).intValue();
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        
	        
	        String content=String.format(getString(R.string.weibo_content_format), entry.title,passed,(int)((float)passed/21f*100));//进度不能大于100%
	        if(passed>21) {
	            content=String.format(getString(R.string.weibo_completed_content_format), entry.title);
	        }
	        String filePath = Tools.shotBitmap(this);

	        Intent intent=new Intent(this,WeiBoShareActivity.class);
	        intent.putExtra(FILE_PATH,filePath);
	        intent.putExtra(CONTENT, content);
	        startActivity(intent);
	 
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
