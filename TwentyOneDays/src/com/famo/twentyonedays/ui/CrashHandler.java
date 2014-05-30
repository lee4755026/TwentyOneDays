package com.famo.twentyonedays.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.R.anim;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.famo.twentyonedays.BuildConfig;
import com.famo.twentyonedays.datacenter.helper.DataBaseHelper;

/**
 * 自定义UncaughtExceptionHandler
 * @author LEE
 * 2014年5月29日 上午10:09:49
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG="CrashHandler";
    private static final boolean DEBUG=BuildConfig.DEBUG;
    /**
     * 系统默认的UncaughtExceptionHandler
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler instance;
    private Context mContext;
    /**
     * 用来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo=new Properties();
    private static final String VERSION_NAME="versionName";
    private static final String VERSION_CODE="versionCode";
    private static final String STACK_TRACE="stackTrace";
    private static final String CRASH_REPORTER_EXTENSION=".cr";
    
    private CrashHandler() {}
    
    /**
     * 单例
     * @return
     */
    public static CrashHandler getInstance() {
        if(instance==null) {
            instance=new CrashHandler();
        }
        return instance;
    }
    
    /**
     * 初始化，注册Context对象
     * @param ctx
     */
    public void init(Context ctx) {
        mContext=ctx;
        mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, ""+ex,ex);
        if(!handleException(ex)&&mDefaultHandler!=null) {
          //如果用户没有处理则让系统默认的异常处理器来处理 
            mDefaultHandler.uncaughtException(thread, ex);
        }else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private boolean handleException(Throwable ex) {
        if(ex==null) {
            Log.w(TAG, "handleException ---ex=null");
            return true;
        }
        final String msg=ex.getLocalizedMessage();
        if(msg==null) {
            return false;
        }
        
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast toast=Toast.makeText(mContext, "程序出错，即将退出:\r\n"+msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Looper.loop();
            };
        }.start();
        
        //收集设备信息
        collectCrashDeviceInfo(mContext);
        //保存错误文件
        saveCrashInfoToFile(ex);
        //发送错误报告到服务器
        sendCrashReportsToServer(mContext);
        return true;
    }

    /**
     * 收集设备信息
     * @param mContext2
     */
    private void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm=ctx.getPackageManager();
            PackageInfo pi=pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if(pi!=null) {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName==null?"not set":pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, ""+pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.d(TAG, "Error while collect package info",e);
        }
        
        //利用反射来收集设备信息，在Build类中包含各种设备信息
         //例如: 系统版本号,设备生产商 等帮助调试程序的有用信息   
        Field[] fields=Build.class.getDeclaredFields();
        for(Field field:fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), ""+field.get(null));
                if(DEBUG)Log.d(TAG, field.getName()+":"+field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e); 
            }
        }
        
    }

    /**
     * 保存错误文件
     * @param ex
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer writer=new StringWriter();
        PrintWriter printWriter=new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause=ex.getCause();
        while(cause!=null) {
            cause.printStackTrace(printWriter);
            cause=cause.getCause();
        }
        String result=writer.toString();
        printWriter.close();
        mDeviceCrashInfo.put("EXCEPTION", ex.getLocalizedMessage());
        mDeviceCrashInfo.put(STACK_TRACE, result);
        try {
            String time=new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String fileName="crash-"+time+CRASH_REPORTER_EXTENSION;
            FileOutputStream trace=mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            mDeviceCrashInfo.store(trace, "");
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file...", e);   
        }
        return null;
        
    }

    /**
     * 发送错误报告到服务器
     * @param mContext2
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles=getCrashReportFiles(ctx);
        if(crFiles!=null&&crFiles.length>0) {
            TreeSet<String> sortedFiles=new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            for(String fileName:sortedFiles) {
                if(DEBUG)Log.d(TAG, "crash 文件 是"+fileName);
                File cr=new File(ctx.getFilesDir(),fileName);
                postReport(cr);
                //TODO:上传成功后再删除
//                cr.delete();
            }
        }
    }

    private String[] getCrashReportFiles(Context ctx) {
        File fileDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return fileDir.list(filter);
    }
    
    private void postReport(File cr) {
        // TODO Auto-generated method stub
        
    }
}
