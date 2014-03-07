/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.famo.twentyonedays.ui.widget.calender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

/**
 * 日期控件单元格
 * @Description: 日期控件单元格

 * @FileName: Cell.java 

 * @Package com.exina.android.calendar 

 * @Author Hanyonglu

 * @Date 2012-3-26 上午11:37:05 

 * @Version V1.0
 */
public class Cell {
	private static final String TAG = "Cell";
	protected Rect mBound = null;
	protected int mDayOfMonth = 1;	// from 1 to 31
	public Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
            |Paint.ANTI_ALIAS_FLAG);
	int dx, dy;
	public Cell(int dayOfMon, Rect rect, float textSize, boolean bold) {
		mDayOfMonth = dayOfMon;
		mBound = rect;
		mPaint.setTextSize(textSize/*26f*/);
		mPaint.setColor(Color.BLACK);
		
		mPaint.setStyle(Style.STROKE);
//		mPaint.setStrokeWidth(2);
		
		if(bold) mPaint.setFakeBoldText(true);
		
		dx = (int) mPaint.measureText(String.valueOf(mDayOfMonth)) / 2;
		dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;
	}
	
	public Cell(int dayOfMon, Rect rect, float textSize) {
		this(dayOfMon, rect, textSize, false);
	}
	
	protected void draw(Canvas canvas) {
		canvas.save();
		Paint paint=new Paint(mPaint);
		paint.setColor(Color.rgb(214, 119, 39));//暗橙色
		paint.setStyle(Style.FILL);
		Rect textBg=new Rect(mBound.centerX() - 25, mBound.centerY()-20, mBound.centerX()+25, mBound.centerY()+25);
		canvas.drawRect(textBg, paint);
		canvas.restore();
		
		canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy, mPaint);
	}
	
	public int getDayOfMonth() {
		return mDayOfMonth;
	}
	
	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y); 
	}
	
	public Rect getBound() {
		return mBound;
	}
	
	public String toString() {
		return String.valueOf(mDayOfMonth)+"("+mBound.toString()+")";
	}
	
}

