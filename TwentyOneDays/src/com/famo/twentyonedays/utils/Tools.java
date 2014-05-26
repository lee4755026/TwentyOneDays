package com.famo.twentyonedays.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;

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
	
	/**
	 * 截图返回BitMap
	 * @param activity
	 * @return
	 */
	public static Bitmap takeScreenShot(Activity activity) {
	    Bitmap bitmap=null;
	    View view=activity.getWindow().getDecorView();
	    // 设置是否可以进行绘图缓存  
        view.setDrawingCacheEnabled(true);  
        // 如果绘图无法缓存，强制构建绘图缓存  
        view.buildDrawingCache();  
	    bitmap=view.getDrawingCache();
	 // 获取状态栏高度  
        Rect frame=new Rect();  
        view.getWindowVisibleDisplayFrame(frame);
        int statusHeight=frame.top;
        DisplayMetrics metrics=new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        bitmap=Bitmap.createBitmap(bitmap, 0, statusHeight+getActionBarSize(activity), metrics.widthPixels, metrics.heightPixels-statusHeight-getActionBarSize(activity));
        
	    return bitmap;
	}
	
	public static boolean savePic(Bitmap bitmap,String fileName) {
	    FileOutputStream outputStream=null;
	    try {
            outputStream=new FileOutputStream(fileName);
            if(outputStream!=null) {
                bitmap.compress(CompressFormat.JPEG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}
	
	public static String shotBitmap(Activity activity) {
	    String fileName="sdcard/"+System.currentTimeMillis()+".jpg";
	    if(Environment.getExternalStorageState()==Environment.MEDIA_MOUNTED) {
	        File dir=new File(Environment.getExternalStorageDirectory()+File.separator+"21DAY");
	        if(!dir.exists()) {
	            if(dir.mkdir()) {
	                File file=new File(dir, "SHOT_"+System.currentTimeMillis()+".jpg");
	                fileName=file.getAbsolutePath();
	            }
	        }
	    }
	    
	    if(savePic(takeScreenShot(activity), fileName)){
	        return fileName;
	    }
	    return   null;
	}
    /**
     * 获取ActionBar的高度
     * @param context
     * @return
     */
    public static int getActionBarSize(Context context) {
        int[] attrs = { android.R.attr.actionBarSize };
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            return values.getDimensionPixelSize(0, 0);// 第一个参数数组索引，第二个参数 默认值
        } finally {
            values.recycle();
        }
    }
}
