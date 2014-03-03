package com.famo.twentyonedays.ui.widget;

import com.famo.twentyonedays.model.PlanEntry;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewCustom extends ListView {
	private SlideViewWidget mFocusedItemView;

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

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x=(int) ev.getX();
			int y=(int) ev.getY();
			int position=pointToPosition(x, y);
			if(position!=INVALID_POSITION){
				PlanEntry entry=(PlanEntry) getItemAtPosition(position);
				mFocusedItemView=entry.slideView;
			}
			break;

		default:
			break;
		}
		if(mFocusedItemView!=null){
			mFocusedItemView.onRequireTouchEvent(ev);
		}
		
		return super.onTouchEvent(ev);
	}
	
	

}
