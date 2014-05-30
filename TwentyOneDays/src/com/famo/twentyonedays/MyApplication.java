package com.famo.twentyonedays;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.famo.twentyonedays.ui.CrashHandler;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class MyApplication extends Application {

    private static final String UPDATE_SERVER = "http://twentydays.duapp.com";
    private static final String UPDATE_PATH_CHECK = "/checkupdate";
    private static final String UPDATE_PATH_FILE = "/fetchapk";
    
    private static final String APP_PACKAGE_NAME = MyApplication.class.getPackage().getName();
    private Logger logger;
    @Override
    public void onCreate() {
        super.onCreate();
        initLogger();
        
        CrashHandler handler=CrashHandler.getInstance();
        handler.init(getApplicationContext());
        
    }
    
    /**
     * 初始化 Logger
     *  ERROR > WARN > INFO > DEBUG
     *  打印高于或等于所设置级别的日志
     */
    private void initLogger() {
        String date = new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date( System.currentTimeMillis() ) );
        //配置log4j日志工具
        LogConfigurator logConfigurator = new LogConfigurator(); 
        logConfigurator.setFileName( getLogPath() + File.separator + date + ".txt" );  
        logConfigurator.setRootLevel( Level.INFO );  
        //设置Logcat日志文件记录级别
        logConfigurator.setLevel( "org.apache", Level.DEBUG);
//        logConfigurator.setLevel("org.apache", Level.INFO);
        logConfigurator.setFilePattern( "%d %-5p [%c{2}]-[%L] %m%n" );  
        logConfigurator.setMaxFileSize( 1024 * 1024 * 5 );  
        logConfigurator.setImmediateFlush( true );  
        logConfigurator.configure();  
        logger = Logger.getLogger( MyApplication.class ); 
        logger.info( APP_PACKAGE_NAME + " Starting..." );
        
        date = null;
    }
    /**
     * 获取 Logs 目录
     * @return
     */
    public String getLogPath(){
        // FIXME 没有 SD 卡时将 Logs 文件存于手机内部存储。
        if ( android.os.Environment.getExternalStorageState().equals( 
                android.os.Environment.MEDIA_MOUNTED ) )
            return Environment.getExternalStorageDirectory()  
                    + File.separator + APP_PACKAGE_NAME 
                    + File.separator + "logs";
        else 
            return getDir( "logs", Context.MODE_PRIVATE ).getPath();
    }
    
    public String getUpdateCheckUrl() {
        return UPDATE_SERVER+UPDATE_PATH_CHECK;
    }
    public String getUpdateFileUrl() {
        return UPDATE_SERVER+UPDATE_PATH_FILE;
    }

    
}
