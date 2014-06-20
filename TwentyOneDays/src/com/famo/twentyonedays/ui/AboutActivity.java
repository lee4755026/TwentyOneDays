package com.famo.twentyonedays.ui;

import android.R.array;
import android.content.Intent;
import android.database.ContentObservable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;

import com.famo.twentyonedays.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.about);

    }

    public void onTextClick(View view) {
        switch (view.getId()) {
        case android.R.id.text2:
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("http://baike.baidu.com/view/1910812.htm?fr=aladdin");
            intent.setData(uri);
            startActivity(intent);
            break;
        case R.id.feedback:
            Intent send = new Intent(Intent.ACTION_SENDTO);
            send.setData(Uri.parse("mailto:fearlesslife@163.com"));
            send.putExtra(Intent.EXTRA_SUBJECT, "21天效应反馈");

            startActivity(send);
            break;
        }
    }

}
