package com.famo.twentyonedays.utils;

import java.text.SimpleDateFormat;
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
}
