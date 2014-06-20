package com.famo.twentyonedays.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 显示计划执行的进度情况 
 * @author LiChaofei 
 * <br/>2014-3-24 下午3:03:21
 */
public class ProgressLayout extends ViewGroup {
private static final String TAG="ProgressLayout";
private static final int COLUMN_COUNT=7;
private static final int HORIZONTAL_SPACE=2;
private static final int VERTICAL_SPACE=2;
private int maxChildWidth=0;
private int maxChildHeight=0;

	public ProgressLayout(Context context) {
		super(context);
	}
	

	public ProgressLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}


	public ProgressLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize=MeasureSpec.getSize(widthMeasureSpec);
		int heightSize=MeasureSpec.getSize(heightMeasureSpec);
		int paddingLeft=this.getPaddingLeft();
		int paddingRight=this.getPaddingTop();
		int paddingTop=this.getPaddingTop();
		int paddingBottom=this.getPaddingBottom();
		
//		Log.d(TAG, "调用onMeasure,width="+widthSize+",height="+heightSize+",paddingLeft="+paddingLeft+",paddingRight="+paddingRight+",paddingTop="+paddingTop+",paddingBottom="+paddingBottom);
		
		
		//类似9宫格的形式
		maxChildHeight=maxChildWidth=(widthSize-paddingLeft-paddingRight)/COLUMN_COUNT-HORIZONTAL_SPACE*2;
		
		int childMeasureWidthSpec=MeasureSpec.makeMeasureSpec(maxChildWidth, MeasureSpec.EXACTLY);
		int childMeasureHeightSpec=MeasureSpec.makeMeasureSpec(maxChildHeight, MeasureSpec.EXACTLY);
		
		int childCount = getChildCount();
		for (int index = 0; index < childCount; index++) {
		final View child = getChildAt(index);
		// measure
		child.measure(childMeasureWidthSpec, childMeasureHeightSpec);
	}
		

		int rowCount=childCount%COLUMN_COUNT==0?childCount/COLUMN_COUNT:childCount/COLUMN_COUNT+1;
		heightSize=(maxChildHeight+VERTICAL_SPACE*2)*rowCount+paddingTop+paddingBottom;
		heightMeasureSpec=MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
		
		setMeasuredDimension(
				resolveSize(widthSize, widthMeasureSpec), 
				resolveSize(heightSize, heightMeasureSpec));
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		Log.d(TAG, "maxChildWidth="+maxChildWidth);
		int paddingLeft=this.getPaddingLeft();
		int paddingTop=this.getPaddingTop();
		int total=getChildCount();
		for(int i=0;i<total;i++){
			final View child=getChildAt(i);
			int row=i/COLUMN_COUNT;
			int colomn=i%COLUMN_COUNT;
			
			int left =paddingLeft+(maxChildWidth+HORIZONTAL_SPACE*2)*colomn+HORIZONTAL_SPACE;
			int top = paddingTop+(maxChildHeight+VERTICAL_SPACE*2)*row+VERTICAL_SPACE;
//			Log.d(TAG, "left="+left+",top="+top);
			child.layout(left, top, left+maxChildWidth, top+maxChildHeight);
		}
	}

}
