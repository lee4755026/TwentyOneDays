package com.famo.twentyonedays.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 工具类
 * @author LiChaofei 
 * <br/>2014-2-28 下午4:54:11
 */
public class Tools {
	public static String currentSystemTime(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
		
	}

	public static String currentWeekDay() {
		String week=null;
		Calendar c=Calendar.getInstance();
		int day=c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.SUNDAY:
			week="星期日";
			break;
		case Calendar.MONDAY:
			week="星期一";
			break;
		case Calendar.TUESDAY:
			week="星期二";
			break;
		case Calendar.WEDNESDAY:
			week="星期三";
			break;
		case Calendar.THURSDAY:
			week="星期四";
			break;
		case Calendar.FRIDAY:
			week="星期五";
			break;
		case Calendar.SATURDAY:
			week="星期六";
			break;

		default:
			break;
		}
		return week;
	}
}
