package com.famo.twentyonedays.datacenter.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DataBaseHelper";
	private static final String DB_NAME = "twentyone.db";
	public  static final String TABLE_PLAN_NAME = "plandata";
	private static final int VERSION = 1;
	
	/**
	 * 列名
	 * create table plandata (
		id integer primary key not null,
		title text not null,
		content text not null,
		startdate integer not null,
		enddate integer not null,
		alarmtime text null,
		color integer null,
		synctime integer null,
		flag integer,
		reserved text null);
	 */
	public static final String COLUMN_ID="id";
	public static final String COLUMN_TITLE="title";
	public static final String COLUMN_CONTENT="content";
	public static final String COLUMN_STARTDATE="startdate";
	public static final String COLUMN_ENDDATE="enddate";
	public static final String COLUMN_REMINDERTIME="remindertime";
	public static final String COLUMN_CREATETIME="createtime";	
	public static final String COLUMN_COLOR="color";
	public static final String COLUMN_SYNCTIME="synctime";
	public static final String COLUMN_FLAG="flag";
	public static final String COLUMN_RESERVED="reserved";

	

	public DataBaseHelper(Context context){
		super(context, DB_NAME, null, VERSION);
	}

	public DataBaseHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql=buildSql();
		Log.d(TAG,sql);
		db.execSQL(sql);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	private static String TEXT_NOT_NULL=" text not null,";
	private static String TEXT_NULL=" text null,";
	private static String INTEGER_NOT_NULL=" integer not null,";
	private static String INTEGER_NULL=" integer null,";
	
	private String buildSql(){
		StringBuilder builder=new StringBuilder();
		builder.append("create table "+TABLE_PLAN_NAME);
		builder.append(" (");
		builder.append(COLUMN_ID+" integer primary key not null,");
		builder.append(COLUMN_TITLE+TEXT_NOT_NULL);
		builder.append(COLUMN_CONTENT+TEXT_NOT_NULL);
		builder.append(COLUMN_STARTDATE+INTEGER_NOT_NULL);
		builder.append(COLUMN_ENDDATE+INTEGER_NOT_NULL);
		builder.append(COLUMN_REMINDERTIME+TEXT_NULL);
		builder.append(COLUMN_CREATETIME+INTEGER_NOT_NULL);
		builder.append(COLUMN_COLOR+INTEGER_NULL);
		builder.append(COLUMN_SYNCTIME+INTEGER_NULL);
		builder.append(COLUMN_FLAG+INTEGER_NULL);
		builder.append(COLUMN_RESERVED+" text null");		
		builder.append(" );");	
		
		return builder.toString();
		
	}

}
