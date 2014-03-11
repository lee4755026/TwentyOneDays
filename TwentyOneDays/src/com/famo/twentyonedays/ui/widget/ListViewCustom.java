package com.famo.twentyonedays.ui.widget;

import com.famo.twentyonedays.model.PlanEntry;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewCustom extends ListView {
	private SlideViewWidget mFocusedItemView;
	private OnItemClickListener mOnItemClickListener;
	private OnItemDeleteClickListener mOnItemDeleteClickListener;
	public ListViewCustom(Context context) {
		super(context);
	}

	public ListViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewCustom(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void shrinkListItem(int position){
		View item=getChildAt(position);
		if(item!=null){
			((SlideViewWidget)item).shrink();			
		}
	}
	
	int position=INVALID_POSITION;
	PlanEntry entry=null;
	boolean handled = false;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x=(int) ev.getX();
			int y=(int) ev.getY();
			position=pointToPosition(x, y);
			if(position!=INVALID_POSITION){
				entry=(PlanEntry) getItemAtPosition(position);
				mFocusedItemView=entry.slideView;
			}
			break;
		default:
			break;
		}
		if(mFocusedItemView!=null){
			mFocusedItemView.onRequireTouchEvent(ev, position);
		}
		
		return super.onTouchEvent(ev);
	}

	/* 
	 * 用SlideViewWidget.OnSlideListener.onClick(int position)的方法实现点击
	 * (non-Javadoc)
	 * @see android.widget.AdapterView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)
	 */
	@Deprecated
	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener=listener;
	}
	
	
	
	public OnItemDeleteClickListener getOnItemDeleteClickListener() {
		return mOnItemDeleteClickListener;
	}

	public void setOnItemDeleteClickListener(
			OnItemDeleteClickListener mOnItemDeleteClickListener) {
		this.mOnItemDeleteClickListener = mOnItemDeleteClickListener;
	}



	/**
	 * 处理删除按钮的点击事件
	 * @author LiChaofei 
	 * <br/>2014-3-10 下午4:36:49
	 */
	public interface OnItemDeleteClickListener{
		void onDelete(int position);
	}

	

}
