package com.famo.twentyonedays.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.utils.AccessTokenKeeper;
import com.famo.twentyonedays.utils.Constants;
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

public class WeiBoShareActivity extends BaseActivity {
    private static final CharSequence YES = "yes";
    private static final String TAG = "WeiBoShareActivity";
    private EditText editText;
    private ImageView imageView;
    private ImageView imageViewZoom;
    
    private WeiboAuth mWeiboAuth;
    public Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private StatusesAPI mStatusesAPI;
    protected Bitmap bitmap;
    private String content;
    private boolean isZoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_share);
        
        findViews();
        bindData();
        bindEvents();
    }
    private void findViews() {
        editText = (EditText) findViewById(android.R.id.text1);
        imageView = (ImageView) findViewById(R.id.image);
        imageViewZoom = (ImageView) findViewById(R.id.image_zoom);
    }
    
    private void bindData() {
       content = getIntent().getStringExtra(DetailActivity.CONTENT);
       bitmap=Tools.readBitmap(getIntent().getStringExtra(DetailActivity.FILE_PATH));
       editText.setText(content);
       imageView.setImageBitmap(bitmap);
    }
    private void bindEvents() {
        imageView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      
                getSupportActionBar().hide();
                imageViewZoom.setImageBitmap(bitmap);
                imageViewZoom.setVisibility(View.VISIBLE);
                ScaleAnimation animation=new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.setDuration(300);
                imageViewZoom.startAnimation(animation);
                isZoom=true;
            }
        });
        imageViewZoom.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                shrindImageZoom();
            }
        });
    }
    
    
    /**
     * 收缩
     */
    private void shrindImageZoom() {
        imageViewZoom.setVisibility(View.GONE);
        imageView.setScaleType(ScaleType.FIT_XY);
        getSupportActionBar().show();
        isZoom=false;
    }
    
    @Override
    public void onBackPressed() {
        if(isZoom) {
            shrindImageZoom();
            return;
        }
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getTitle().equals(YES)) {
           Log.d(TAG, "分享");
           onShareClick();
       }
        return super.onOptionsItemSelected(item);
    }

 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItemCompat.setShowAsAction(
            menu.add(YES)
            .setIcon(R.drawable.abc_ic_cab_done_holo_light)
            , MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }
    

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
                        Toast.makeText(WeiBoShareActivity.this,"获取微博信息流成功, 条数: " + statuses.statusList.size(),Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
//                    Toast.makeText(WeiBoShareActivity.this,"发送一送微博成功, id = " + status.id,Toast.LENGTH_LONG).show();
                    Toast.makeText(WeiBoShareActivity.this,"发送一送微博成功",Toast.LENGTH_LONG).show();
                    
                    if(!bitmap.isRecycled()) {//TODO:寻找位置 
                        bitmap.recycle();
                        bitmap=null;
                    }
                } else {
                    Toast.makeText(WeiBoShareActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WeiBoShareActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
    private void onShareClick() {
        Log.d(TAG, "分享到新浪微博...");

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if(mAccessToken!=null&&mAccessToken.isSessionValid()) {
        // 对statusAPI实例化
            post();
        }else {
//        ssoAuthorize();
        webAuthorize();
        }
    }


    private void ssoAuthorize() {
        mSsoHandler = new SsoHandler(WeiBoShareActivity.this, mWeiboAuth);
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
    
    private void post() {
        
        mStatusesAPI = new StatusesAPI(mAccessToken);
      mStatusesAPI.upload(content, bitmap, null, null, mListener);
    }
    
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                Log.d(TAG,"mAccessToken="+ mAccessToken.toString());
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WeiBoShareActivity.this, mAccessToken);
                post();

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
