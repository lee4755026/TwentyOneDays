/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.Templates;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.utils.Tools;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MonthDisplayHelper;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 日期控件绘制类
 * @Description: 日期控件绘制类

 * @FileName: CalendarView.java 

 * @Package com.exina.android.calendar 

 * @Author Hanyonglu

 * @Date 2012-3-26 上午11:37:22 

 * @Version V1.0
 */
public class CalendarView extends ImageView {
    private static int WEEK_TOP_MARGIN = 74;
    private static int WEEK_LEFT_MARGIN = 40;
    private static int CELL_WIDTH = 60;
    private static int CELL_HEIGH = 53;
    private static int CELL_MARGIN_TOP = 0;
    private static int CELL_MARGIN_LEFT = 39;
    private static float CELL_TEXT_SIZE;
    
    
    public static final int CURRENT_MOUNT = 0;
    public static final int NEXT_MOUNT = 1;
    public static final int PREVIOUS_MOUNT = -1;
	private static final String TAG = "CalendarView"; 
	private Calendar mRightNow = null;
    private Drawable mWeekTitle = null;
    private Cell mToday = null;
    private Cell[][] mCells = new Cell[6][7];
    private OnCellTouchListener mOnCellTouchListener = null;
    MonthDisplayHelper mHelper;
    Drawable mDecoration = null;
    public Drawable mDecoraClick = null;
    private Context context;
    private Calendar planStart;
    private Calendar planEnd;
	public interface OnCellTouchListener {
    	public void onTouch(Cell cell);
    }

	public CalendarView(Context context) {
		this(context, null);
	}
	
	public CalendarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		mDecoration = context.getResources().getDrawable(R.drawable.typeb_calendar_today);	 
		mDecoraClick = context.getResources().getDrawable(R.drawable.typeb_calendar_today);
		Display display=((Activity)context).getWindowManager().getDefaultDisplay();
		Log.d(TAG, "display width="+display.getWidth()+",height="+display.getHeight());
		initCalendarView();
	}
	
	private void initCalendarView() {
		mRightNow = Calendar.getInstance();
		// prepare static vars
		Resources res = getResources();
		WEEK_TOP_MARGIN  = (int) res.getDimension(R.dimen.week_top_margin);
		WEEK_LEFT_MARGIN = (int) res.getDimension(R.dimen.week_left_margin);
		
		CELL_WIDTH = (int) res.getDimension(R.dimen.cell_width);
		CELL_WIDTH=(context.getResources().getDisplayMetrics().widthPixels-getPaddingLeft()-getPaddingRight())/7;
		CELL_HEIGH = (int) res.getDimension(R.dimen.cell_heigh);
		CELL_HEIGH=(int) (CELL_WIDTH*0.75);
		CELL_MARGIN_TOP = (int) res.getDimension(R.dimen.cell_margin_top);
		CELL_MARGIN_LEFT = (int) res.getDimension(R.dimen.cell_margin_left);
		CELL_MARGIN_LEFT=0;

		CELL_TEXT_SIZE = res.getDimension(R.dimen.cell_text_size);
		// set background
		setImageResource(R.drawable.calendar_background);
//		mWeekTitle = res.getDrawable(R.drawable.calendar_week);
		
		mHelper = 
				new MonthDisplayHelper(
						mRightNow.get(Calendar.YEAR),
						mRightNow.get(Calendar.MONTH),
						mRightNow.getFirstDayOfWeek()
					);

    }
	
	private void initCells() {
		Log.d(TAG, "initCells "+Tools.currentSystemTime());
	    class _calendar {
	    	public int day;
	    	public int whichMonth;  // -1 为上月  1为下月  0为此月
	    	public _calendar(int d, int b) {
	    		day = d;
	    		whichMonth = b;
	    	}
	    	public _calendar(int d) { // 上个月 默认为
	    		this(d, PREVIOUS_MOUNT);
	    	}
	    };
	    _calendar tmp[][] = new _calendar[6][7];
	    
	    for(int i=0; i<tmp.length; i++) {
	    	int n[] = mHelper.getDigitsForRow(i);
	    	for(int d=0; d<n.length; d++) {
	    		if(mHelper.isWithinCurrentMonth(i,d))
	    			tmp[i][d] = new _calendar(n[d], CURRENT_MOUNT);
	    		else if(i == 0) {
	    			tmp[i][d] = new _calendar(n[d]);
	    		} else {
	    			tmp[i][d] = new _calendar(n[d], NEXT_MOUNT);
	    		}
	    		
	    	}
	    }

	    Calendar today = Calendar.getInstance();
	    int thisDay = 0;
	    mToday = null;
	    if(mHelper.getYear()==today.get(Calendar.YEAR) && mHelper.getMonth()==today.get(Calendar.MONTH)) {
	    	thisDay = today.get(Calendar.DAY_OF_MONTH);
	    }
		// build cells
		Rect Bound = new Rect(CELL_MARGIN_LEFT, CELL_MARGIN_TOP, CELL_WIDTH+CELL_MARGIN_LEFT, CELL_HEIGH+CELL_MARGIN_TOP);
		for(int week=0; week<mCells.length; week++) {
			for(int day=0; day<mCells[week].length; day++) {
				if(tmp[week][day].whichMonth == CURRENT_MOUNT) { // 此月  开始设置cell
					if(day==0 || day==6 )
						mCells[week][day] = new RedCell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
					else 
						mCells[week][day] = new Cell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
				} else if(tmp[week][day].whichMonth == PREVIOUS_MOUNT) {  // 上月为gray
					mCells[week][day] = new GrayCell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
				} else { // 下月为LTGray
					mCells[week][day] = new LTGrayCell(tmp[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
				}
				
				Bound.offset(CELL_WIDTH, 0); // move to next column 
				
				// get today
				if(tmp[week][day].day==thisDay && tmp[week][day].whichMonth == 0) {
					mToday = mCells[week][day];
					mDecoration.setBounds(mToday.getBound());
				}
				
				Calendar tempCalendar;
				if(mHelper.isWithinCurrentMonth(week,day)){
					tempCalendar=Calendar.getInstance();
					tempCalendar.set(mHelper.getYear(), mHelper.getMonth(), mHelper.getDayAt(week, day));
				}else if(week == 0) {
					//上个月
					tempCalendar=Calendar.getInstance();
					tempCalendar.set(mHelper.getYear(), mHelper.getMonth(), mHelper.getDayAt(week, day));
					tempCalendar.add(Calendar.MONTH, -1);
	    		} else {
	    			//下个月
	    			tempCalendar=Calendar.getInstance();
					tempCalendar.set(mHelper.getYear(), mHelper.getMonth(), mHelper.getDayAt(week, day));
					tempCalendar.add(Calendar.MONTH, 1);
	    		}
				mCells[week][day].inRange=tempCalendar.after(planStart)&&tempCalendar.before(planEnd);
				mCells[week][day].isPassed=mCells[week][day].inRange&&tempCalendar.before(today);
			}
			Bound.offset(0, CELL_HEIGH); // move to next row and first column
			Bound.left = CELL_MARGIN_LEFT;
			Bound.right = CELL_MARGIN_LEFT+CELL_WIDTH;
		}		
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		android.util.Log.d(TAG, "left="+left);
//		Rect re = getDrawable().getBounds();
//		WEEK_LEFT_MARGIN = CELL_MARGIN_LEFT = (right-left - re.width()) / 2;
		
		
//		mWeekTitle.setBounds(WEEK_LEFT_MARGIN, WEEK_TOP_MARGIN, WEEK_LEFT_MARGIN+mWeekTitle.getMinimumWidth(), WEEK_TOP_MARGIN+mWeekTitle.getMinimumHeight());
		Log.d(TAG, "view width="+getWidth()+",height="+getHeight());
		CELL_WIDTH=getWidth()/7;
		CELL_HEIGH=(int) (CELL_WIDTH*0.75);
		initCells();
		super.onLayout(changed, left, top, right, bottom);
	}
	
    public void setTimeInMillis(long milliseconds) {
    	mRightNow.setTimeInMillis(milliseconds);
    	initCells();
    	this.invalidate();
    }
        
    public int getYear() {
    	return mHelper.getYear();
    }
    
    public int getMonth() {
    	return mHelper.getMonth();
    }
    
    public void nextMonth() {
    	mHelper.nextMonth();
    	initCells();
    	invalidate();
    }
    
    public void previousMonth() {
    	mHelper.previousMonth();
    	initCells();
    	invalidate();
    }
    
    public boolean firstDay(int day) {
    	return day==1;
    }
    
    public boolean lastDay(int day) {
    	return mHelper.getNumberOfDaysInMonth()==day;
    }
    
    public void goToday() {
    	Calendar cal = Calendar.getInstance();
    	mHelper = new MonthDisplayHelper(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    	initCells();
    	invalidate();
    }
    
    public Calendar getDate() {
    	return mRightNow;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(mOnCellTouchListener!=null){
	    	for(Cell[] week : mCells) {
				for(Cell day : week) {
					if(day.hitTest((int)event.getX(), (int)event.getY())) {
						mOnCellTouchListener.onTouch(day);
					}						
				}
			}
    	}
    	return super.onTouchEvent(event);
    }
  
    public void setOnCellTouchListener(OnCellTouchListener p) {
		mOnCellTouchListener = p;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw background
		super.onDraw(canvas);
		
		//绘制当前年-月
		drawCurrentYearMonth(canvas);
		
//		mWeekTitle.draw(canvas);
		drawWeekTitle(canvas);
		
		// draw cells
		for(Cell[] week : mCells) {
			for(Cell day : week) {
				day.draw(canvas);			
			}
		}
		
		// draw today
		if(mDecoration!=null && mToday!=null) {
			mDecoration.draw(canvas);
		}
		if(mDecoraClick.getBounds()!=null) {
			mDecoraClick.draw(canvas);
			// 设置这里过后  要想办法
			mDecoraClick = context.getResources().getDrawable(R.drawable.typeb_calendar_today);
//			mDecoraClick.setBounds(null);
		}
	}

	/**
	 * 绘制当前的年-月
	 * @author LiChaofei
	 * <br/>2014-3-6 下午6:47:20
	 * @param canvas
	 */
	private void drawCurrentYearMonth(Canvas canvas) {
		Paint paint=new Paint(Paint.SUBPIXEL_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(30f);
		paint.setColor(Color.WHITE);
		
		paint.setStyle(Style.STROKE);
		
		String text = getYear()+"年-"+(getMonth()+1)+"月";
		
		int dx = (int) paint.measureText(String.valueOf(text)) / 2;
		int dy = (int) (-paint.ascent() + paint.descent()) / 2;
//		Rect re = getDrawable().getBounds();
		canvas.drawText(text, getWidth()/2-dx, WEEK_TOP_MARGIN-10-dy, paint);
	}
	
	/**
	 * 绘制星期
	 * @param canvas
	 * @author LEE 
	 * </br>2014-4-15 下午9:45:24
	 */
	private void drawWeekTitle(Canvas canvas) {
		String weeks[]={"SUN","MON","TUE","WED","THU","FRI","SAT"};
		Paint paint=new Paint(Paint.SUBPIXEL_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(26f);
		paint.setColor(Color.WHITE);
		
		paint.setStyle(Style.STROKE);
		String text="";
		for(int i=0;i<weeks.length;i++){
			text = weeks[i];
			int dx = (int) paint.measureText(text) / 2;
			int dy = (int) (-paint.ascent() + paint.descent()) / 2;
			if(i>0&&i<6){
				paint.setStyle(Style.FILL);
				paint.setColor(Color.rgb(215, 119, 40));
			}else{
				paint.setStyle(Style.FILL);
				paint.setColor(Color.rgb(228, 128, 48));
			}
			Rect background=new Rect(CELL_WIDTH*i, WEEK_TOP_MARGIN+35-2*dy-20 , CELL_WIDTH*(i+1), WEEK_TOP_MARGIN+35-dy+10);
			canvas.drawRect(background, paint);
			paint.setColor(Color.WHITE);
			canvas.drawText(text, CELL_WIDTH*i+CELL_WIDTH / 2 - dx, WEEK_TOP_MARGIN+35 - dy,paint);
		}
	}

	
	private class GrayCell extends Cell {
		public GrayCell(int dayOfMon, Rect rect, float s) {
			super(dayOfMon, rect, s);
			mPaint.setColor(Color.GRAY);
//			mPaint.setColor(Color.rgb(214, 117, 28));
		}			
	}
	
	
	private class LTGrayCell extends Cell {
		public LTGrayCell(int dayOfMon, Rect rect, float s) {
			super(dayOfMon, rect, s);
			mPaint.setColor(Color.LTGRAY);
		}			
	}
	
	private class RedCell extends Cell {
		public RedCell(int dayOfMon, Rect rect, float s) {
			super(dayOfMon, rect, s);
			mPaint.setColor(0xdddd0000);
		}			
		
	}

	/**
	 * 设置计划的开始时间和结束时间
	 * @author LiChaofei
	 * <br/>2014-3-11 下午4:49:37
	 * @param startDate
	 * @param endDate
	 */
	public void setRange(String startDate, String endDate) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		Date start = null;
		Date end = null;
		try {
			start = sdf.parse(startDate);
			end=sdf.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		planStart=Calendar.getInstance();
		if(start!=null){
			planStart.setTime(start);
		}
		planEnd=Calendar.getInstance();
		if(end!=null){
			planEnd.setTime(end);
		}
    	initCells();
		this.invalidate();
		
	}

}