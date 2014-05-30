package com.famo.twentyonedays.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.famo.twentyonedays.BuildConfig;
import com.famo.twentyonedays.MyApplication;
import com.famo.twentyonedays.R;
import com.famo.twentyonedays.http.CustomHttpClient;
import com.famo.twentyonedays.model.VersionEntity;
import com.famo.twentyonedays.utils.MyDialog;
import com.google.gson.Gson;

public class SplashActivity extends Activity {
    public static final String TAG = "SplashActivity";
    public static final boolean DEBUG = BuildConfig.DEBUG;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        
        setContentView(R.layout.activity_splash);
        
        findViews();
        bindEvents();
        
        updateCheck();
    }

    private void findViews() {
        button=(Button) findViewById(android.R.id.button1);
        
    }

    private void bindEvents() {
       button.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
           Intent intent=new Intent(SplashActivity.this, MainActivity.class);
           startActivity(intent);
           finish();
        }
    });
        
    }
    

    /**
     * 检测版本更新
     */
    private void updateCheck() {
        new CheckTask().execute();
        
    }
    
    private VersionEntity getCurrentVersion() {
        VersionEntity entity=new VersionEntity();
        try {
            PackageManager pm=getPackageManager();
            PackageInfo info=pm.getPackageInfo(getPackageName(),0);
            entity.versionCode=info.versionCode;
            entity.versionName=info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
        return entity;
    }
    
    class CheckTask extends AsyncTask<Void, Void, VersionEntity>{
        private  Gson gson=new Gson();
        @Override
        protected VersionEntity doInBackground(Void... params) {
            String url=((MyApplication)getApplication()).getUpdateCheckUrl();
            String result=CustomHttpClient.get(url, null);
            if(DEBUG)Log.d(TAG, "result="+result);
            return result!=null?gson.fromJson(result, VersionEntity.class):new VersionEntity();
        }
        @Override
        protected void onPostExecute(VersionEntity result) {
            if(DEBUG)Log.d(TAG, "result="+result);
            final String updateUrl=result.updateUrl;
            if(result.compareTo(getCurrentVersion())>0) {
                MyDialog.Confirm(SplashActivity.this, "发现新版本，是否更新", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path=((MyApplication)getApplication()).getUpdateFileUrl();
                        Uri uri=Uri.parse(path+"?file="+updateUrl);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);                        
                    }
                }, null).show();
                
            }
        }
        
        
        
    }

}
