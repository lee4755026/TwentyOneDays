package com.famo.twentyonedays.ui.widget;

import java.util.Date;

import com.famo.twentyonedays.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 用于显示进度的小方格
 * @author LEE
 * 2014年6月20日 下午3:09:09
 */
public class ProgressUnit extends TextView {

    private Date planDate;
    private static final Date today=new Date();
    private static final String TAG = "ProgressUnit";
    public ProgressUnit(Context context) {
        super(context);
    }
    
    
    public void setPlanDate(Date date) {
        planDate=date;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "planDate="+planDate+",today="+today);
        if(planDate.before(today)){//已完成
            this.setBackgroundResource(R.drawable.text_done_background);
        }else {
            this.setBackgroundResource(R.drawable.text_background);
        }
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setGravity(Gravity.CENTER);
        this.setClickable(true);
        this.setTextColor(getResources().getColorStateList(R.drawable.text_color));
//        this.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        super.onLayout(changed, left, top, right, bottom);
    }
    
    
    
    
    
    

}
