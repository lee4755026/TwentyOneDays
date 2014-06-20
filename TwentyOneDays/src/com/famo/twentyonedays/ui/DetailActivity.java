package com.famo.twentyonedays.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.graphics.Bitmap;
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

	private static final String TAG = "DetailActivity";
	private TextView title;
	private TextView content;
	private TextView alarmTime;
//	private CalendarView calendarView;
	private ProgressLayout progressLayout;
	
	private PlanEntry entry;
	private int planId;
	private String planName;
    private WeiboAuth mWeiboAuth;
    public Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private StatusesAPI mStatusesAPI;
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(DetailActivity.this,"获取微博信息流成功, 条数: " + statuses.statusList.size(),Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(DetailActivity.this,"发送一送微博成功, id = " + status.id,Toast.LENGTH_LONG).show();
                    
                    if(!bitmap.isRecycled()) {//TODO:寻找位置 
                        bitmap.recycle();
                        bitmap=null;
                    }
                } else {
                    Toast.makeText(DetailActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(DetailActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
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
	            Toast.makeText(DetailActivity.this, "share content to sina", Toast.LENGTH_SHORT).show();
	            onShareClick();
	            break;
	        }
	        return super.onOptionsItemSelected(item); 
	    } 
	
	private void onShareClick() {
        Log.d(TAG, "分享到新浪微博...");

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if(mAccessToken!=null&&mAccessToken.isSessionValid()) {
        // 对statusAPI实例化
            postContent();
        }else {
//        ssoAuthorize();
        webAuthorize();
        }
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
        
        
        String content=String.format(getString(R.string.weibo_content_format), entry.title,passed,(int)((float)passed/21f*100));
        mStatusesAPI = new StatusesAPI(mAccessToken);
        bitmap = Tools.takeScreenShot(this);
        mStatusesAPI.upload(content, bitmap, null, null, mListener);
 
    }

    private void ssoAuthorize() {
        mSsoHandler = new SsoHandler(DetailActivity.this, mWeiboAuth);
        mSsoHandler.authorize(new AuthDialogListener());
        
    }

    /**
     * 授权
     */
    private void webAuthorize() {
        mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mWeiboAuth.anthorize(new AuthDialogListener());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
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
    
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                Log.d(TAG,"mAccessToken="+ mAccessToken.toString());
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(DetailActivity.this, mAccessToken);
                postContent();

            } else {
            // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code", "");
                Log.d(TAG, "code="+code);
//                .........
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
        }
    }

}
