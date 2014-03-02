package com.famo.twentyonedays.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class FrameViewGroupBackUp extends RelativeLayout {
	private static final String TAG="FrameViewGroup";
	private static final int SNAP_VELOCITY = 600;
	private Context mContext;
	private Scroller mScroller;
	
	
	private int mTouchSlop = 0 ;
	private float mLastMotionX;
	private VelocityTracker mVelocityTracker;

	public FrameViewGroupBackUp(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public FrameViewGroupBackUp(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	
	private void init() {
		mScroller=new Scroller(mContext);
		
//		LinearLayout front=new LinearLayout(mContext);
//		front.setBackgroundColor(Color.RED);
//		addView(front);
//		LinearLayout back=new LinearLayout(mContext);
//		back.setBackgroundColor(Color.BLUE);
//		addView(back);
		
		mTouchSlop=ViewConfiguration.get(mContext).getScaledTouchSlop();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int height=MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		
		int childCount=getChildCount();
		for(int i=0;i<childCount;i++){
			View child=getChildAt(i);
			child.measure(getWidth(), getHeight());
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childCount = getChildCount();
		Log.i(TAG, "--- onLayout childCount is -->" + childCount );

		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout(0, 0, getWidth(), getHeight());
		}
	}
	// 只有当前LAYOUT中的某个CHILD导致SCROLL发生滚动，才会致使自己的COMPUTESCROLL被调用
	@Override
	public void computeScroll() {	
		// 如果返回true，表示动画还没有结束
		// 因为前面startScroll，所以只有在startScroll完成时 才会为false
		if (mScroller.computeScrollOffset()) {
			Log.d(TAG, mScroller.getCurrX() + "======" + mScroller.getCurrY());
			// 产生了动画效果 每次滚动一点
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			
		    //刷新View 否则效果可能有误差
			postInvalidate();
		}
		else
			Log.i(TAG, "have done the scoller -----");
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mVelocityTracker==null){
			mVelocityTracker=VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		
		float x=event.getX();
		float y=event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(mScroller!=null){
				if(!mScroller.isFinished()){
					mScroller.abortAnimation();
				}
			}
			mLastMotionX=x;
			break;
		case MotionEvent.ACTION_MOVE:
			int detaX=(int) (mLastMotionX-x);
			scrollBy(detaX, 0);
			mLastMotionX=x;
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker=mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX=(int) velocityTracker.getXVelocity();
			//快速右滑
			if(velocityX>SNAP_VELOCITY){
				snapToScreen(-1);
			//快速左滑
			}else if(velocityX<-SNAP_VELOCITY){
				snapToScreen(1);
			//慢速滑动
			}else{
				snapToDestination();
			}
			
			
			break;
		case MotionEvent.ACTION_CANCEL:
			
			break;
		}
		
		return true;
	}
	private void snapToDestination(){
		int scrollX = getScrollX() ;
		int scrollY = getScrollY() ;
		
		//判断是否超过下一屏的中间位置，如果达到就抵达下一屏，否则保持在原屏幕	
		//直接使用这个公式判断是哪一个屏幕 前后或者自己
		//判断是否超过下一屏的中间位置，如果达到就抵达下一屏，否则保持在原屏幕
		// 这样的一个简单公式意思是：假设当前滑屏偏移值即 scrollCurX 加上每个屏幕一半的宽度，除以每个屏幕的宽度就是
		//  我们目标屏所在位置了。 假如每个屏幕宽度为320dip, 我们滑到了500dip处，很显然我们应该到达第二屏	
		int direction=0;
		if(scrollX>160){
			direction=1;
		}else if(scrollX<=-160){
			direction=-1;
		}
		snapToScreen(direction);
	}
	/**
	 * 切屏
	 * @author LiChaofei
	 * <br/>2014-2-25 下午3:39:57
	 * @param direction TODO
	 */
	private void snapToScreen(int direction) {
		if(direction==0){
			mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 1*1000);
		}else{		
		int dx = getWidth() - getScrollX();
//		int dx = getWidth();
		Log.d(TAG, "getScroll()="+getScrollX()+",dx="+dx);
		mScroller.startScroll(getScrollX(), 0, (direction)*dx, 0, 1*1000);
		}
		// 此时需要手动刷新View 否则没效果
		invalidate();
		
	}
	
	
	
	

}
