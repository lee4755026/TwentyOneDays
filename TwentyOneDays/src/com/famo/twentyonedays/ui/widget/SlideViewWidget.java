package com.famo.twentyonedays.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.model.PlanEntry;

/**
 * 滑动布局
 * 
 * @author LEE 2014-3-2 下午3:12:04
 */
public class SlideViewWidget extends LinearLayout {
	private static final String TAG = "SlideViewWidget";
	private static final int SNAP_VELOCITY = 600;
	/**
	 * tan=deltaX/deltaY;
	 * tan>2时才横向滑动
	 */
	private static final int TAN = 2;

	/**
	 * 滑动的距离/底层视图的宽度
	 */
	private int mHolderWidth = 120;
	private Context mContext;
	private Scroller mScroller;

	private int mLastX;
	private int mLastY;
	private int mDownX;
	private LinearLayout mViewContent;
	private OnSlideListener mOnSlideListener;

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

	private void init() {
		mScroller = new Scroller(mContext);
		
		setOrientation(LinearLayout.HORIZONTAL);
		View.inflate(mContext, R.layout.slide_view_merge, this);
		mViewContent=(LinearLayout) findViewById(R.id.view_content);
		mHolderWidth=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources().getDisplayMetrics()));
		
	}
	/**
	 * 添加内容视图
	 * @author LiChaofei
	 * <br/>2014-3-3 上午11:04:50
	 * @param view
	 */
	public void setContentView(View view){
		mViewContent.addView(view);
	}
	
    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }
    
    public ViewGroup getContentView() {
        return mViewContent;
    }
    
    /**
     * 关闭
     * @author LiChaofei
     * <br/>2014-3-3 上午11:06:47
     */
    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }
	/**
	 * 缓慢滚动到指定位置
	 * @author LiChaofei
	 * <br/>2014-3-3 上午11:08:32
	 * @param destX
	 * @param destY
	 */
	private void smoothScrollTo(int destX,int destY){
		int scrollX=getScrollX();
		int delta=destX-scrollX;
		mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta)*3);
	}

	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			 //刷新View 否则效果可能有误差
			postInvalidate();
		}
	}


	public void onRequireTouchEvent(MotionEvent event, int position){
		int x=(int) event.getX();
		int y=(int) event.getY();
		int scrollX=getScrollX();
		 Log.d(TAG, "x=" + x + "  y=" + y+" scorllX="+scrollX);
		 
		 switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			if(mOnSlideListener!=null){
				mOnSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_START_SCROLL);
			}
			mDownX=x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX=x-mLastX;
			int deltaY=y-mLastY;
			if(Math.abs(deltaX)<Math.abs(deltaY)*TAN){
				break;
			}
			int newScrollX=scrollX-deltaX;
			if(deltaX!=0){
				if(newScrollX<0){
					newScrollX=0;
				}else if(newScrollX>mHolderWidth){
					newScrollX=mHolderWidth;
				}
				this.scrollTo(newScrollX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			int deltaScrollX=0;
			if(scrollX-mHolderWidth*0.75>0){
				deltaScrollX=mHolderWidth;
			}
			this.smoothScrollTo(deltaScrollX, 0);
			if(mOnSlideListener!=null){
				mOnSlideListener.onSlide(this, deltaScrollX==0?OnSlideListener.SLIDE_STATUS_OFF:OnSlideListener.SLIDE_STATUS_ON);
				
				if(Math.abs(x-mDownX)<10){//微小的滑动视为点击
					shrink();
					mOnSlideListener.onClick(position);
				}
			}
			break;
		default:
			break;
		}
		 mLastX = x;
	     mLastY = y;
	     
	}

	
	 public interface OnSlideListener {
	        public static final int SLIDE_STATUS_OFF = 0;
	        public static final int SLIDE_STATUS_START_SCROLL = 1;
	        public static final int SLIDE_STATUS_ON = 2;

	        /**
	         * @param view current SlideView
	         * @param status SLIDE_STATUS_ON or SLIDE_STATUS_OFF
	         */
	        public void onSlide(View view, int status);
	        public void onClick(int position);
	    }

}
