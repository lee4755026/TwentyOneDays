package com.famo.twentyonedays.ui.widget;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Scroller;

/**
 * 滑动布局
 * 
 * @author LEE 2014-3-2 下午3:12:04
 */
public class SlideViewWidget extends ViewGroup {
	private static final String TAG = "SlideViewWidget";
	private static final int SNAP_VELOCITY = 600;
	private Context mContext;
	private Scroller mScroller;

	private int mTouchSlop = 0;
	private float mLastMotionX;
	private VelocityTracker mVelocityTracker;
	private int mCurrentScreen=0;

	public SlideViewWidget(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public SlideViewWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public SlideViewWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		mScroller = new Scroller(mContext);

		mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);

		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(getWidth(), getHeight());
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childCount = getChildCount();
		Log.i(TAG, "--- onLayout childCount is -->" + childCount);
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout(i * getWidth(), 0, (i+1)*getWidth(), getHeight());
		}
	}
	
	

	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			 //刷新View 否则效果可能有误差
			postInvalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		float x = event.getX();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mScroller != null) {
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
			}
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int detaX = (int) (mLastMotionX - x);
			scrollBy(detaX, 0);
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY) {
				Log.d(TAG, "向右滑"+velocityX);
//				snapToScreen(0);
			} else if (velocityX < -SNAP_VELOCITY) {
				Log.d(TAG, "向左滑"+velocityX);
//				snapToScreen(1);
			} else {
				snapToDestination();
			}
			snapToDestination();
			break;
		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return true;
	}

	private void snapToDestination() {
		int scrollX=getScrollX();
		Log.d(TAG, "scrollX="+scrollX);
//		if(mCurrentScreen==0){
			
			if(scrollX>100){//滑动至下一屏
				snapToScreen(1);
			}else{
				snapToScreen(0);
			}
//		}

	}

	private void snapToScreen(int screen) {
		if(screen==1){
			//下一屏显示一半
			int dx = getWidth()/2 - getScrollX();
			Log.d(TAG, "getScroll()="+getScrollX()+",dx="+dx);
			mScroller.startScroll(getScrollX(), 0, dx, 0, 1*1000);
		}else{		
			mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 1*1000);
		
		}
		// 此时需要手动刷新View 否则没效果
		invalidate();

	}

}
