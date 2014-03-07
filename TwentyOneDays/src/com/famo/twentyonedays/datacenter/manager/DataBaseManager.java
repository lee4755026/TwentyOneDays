package com.famo.twentyonedays.datacenter.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.famo.twentyonedays.BuildConfig;
import com.famo.twentyonedays.datacenter.helper.DataBaseHelper;
import com.famo.twentyonedays.model.PlanEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseManager {
	private static final int ERROR_ID = -1;
	private static final boolean DEBUG = BuildConfig.DEBUG;
	private static final String TAG = "DataBaseManager";
	private SQLiteDatabase db;
	private DataBaseHelper helper;

	public DataBaseManager(Context context) {
		helper=new DataBaseHelper(context);
	}
	private void open(){
		if(db==null){
			db=helper.getWritableDatabase();
		}
	}
	private void close(){
		if(db!=null&&db.isOpen()){
			db.close();
		}
	}
	/**
	 * 根据ID获取记录
	 * @author LiChaofei
	 * <br/>2014-3-7 下午2:43:46
	 * @param id
	 * @return
	 */
	public PlanEntry getDataById(int id){
		PlanEntry entry=new PlanEntry();
		try {
			open();
			Cursor cursor=db.query(DataBaseHelper.TABLE_PLAN_NAME, null, DataBaseHelper.COLUMN_ID+"=?", new String[]{String.valueOf(id)}, null, null, null);
			if(cursor.moveToNext()){
				extraValues(cursor, entry);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			close();
		}
		return entry;
	}
	/**
	 * 获取所有的记录
	 * @author LiChaofei
	 * <br/>2014-3-7 下午3:53:29
	 * @return
	 */
	public List<PlanEntry> getPlanEntries(){
		List<PlanEntry> list=new ArrayList<PlanEntry>();
		try {
			open();
			Cursor cursor=db.query(DataBaseHelper.TABLE_PLAN_NAME, null, null, null, null, null, "createtime desc");
			while (cursor.moveToNext()) {
				PlanEntry entry=new PlanEntry();
				extraValues(cursor, entry);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	/**
	 * 将cursor里的值填充到entry里
	 * @author LiChaofei
	 * <br/>2014-3-7 下午3:52:51
	 * @param cursor
	 * @param entry
	 */
	private void extraValues(Cursor cursor, PlanEntry entry) {
		entry.id=cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_ID));
		entry.title=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_TITLE));
		entry.content=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_CONTENT));
		entry.startDate=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_STARTDATE));
		entry.endDate=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_ENDDATE));
		entry.reminderTime=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_REMINDERTIME));
		entry.createTime=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_CREATETIME));
		entry.color=cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_COLOR));
		entry.syncTime=new Date(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_SYNCTIME)));
		entry.flag=cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_FLAG));
		entry.reserved=cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_RESERVED));
	}
	/**
	 * 新增
	 * @author LiChaofei
	 * <br/>2014-3-7 上午11:34:50
	 * @param entry
	 * @return
	 */
	public boolean insert(PlanEntry entry){
		if(entry==null)return false;
		long id=ERROR_ID;
		ContentValues values=new ContentValues();
//		values.put(DataBaseHelper.COLUMN_ID, entry.id);
		values.put(DataBaseHelper.COLUMN_TITLE, entry.title);
		values.put(DataBaseHelper.COLUMN_CONTENT, entry.content);
		values.put(DataBaseHelper.COLUMN_CREATETIME, entry.createTime);
		values.put(DataBaseHelper.COLUMN_STARTDATE, entry.startDate);
		values.put(DataBaseHelper.COLUMN_ENDDATE, entry.endDate);
		values.put(DataBaseHelper.COLUMN_REMINDERTIME, entry.reminderTime);
		values.put(DataBaseHelper.COLUMN_COLOR, entry.color);
		if(entry.syncTime!=null){
			values.put(DataBaseHelper.COLUMN_SYNCTIME, entry.syncTime.getTime());
		}
		values.put(DataBaseHelper.COLUMN_FLAG, entry.flag);
		values.put(DataBaseHelper.COLUMN_RESERVED, entry.reserved);
		try {
			open();
			id=db.insert(DataBaseHelper.TABLE_PLAN_NAME, null, values);
			if(DEBUG)Log.d(TAG, "新插入记录id="+id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			close();
		}
		
		return id!=ERROR_ID;
		
	}
	/**
	 * 更新（暂不提供该功能）
	 * @author LiChaofei
	 * <br/>2014-3-7 上午11:34:23
	 * @param entry
	 * @return
	 */
	public boolean update(PlanEntry entry){
		return false;
		
	}
	/**
	 * 删除
	 * @author LiChaofei
	 * <br/>2014-3-7 上午11:34:16
	 * @param id
	 * @return
	 */
	public boolean delete(int id){
		long deleteRows=0;
		try {
			open();
			id=db.delete(DataBaseHelper.TABLE_PLAN_NAME, "id=?", new String[]{String.valueOf(id)});
			if(DEBUG)Log.d(TAG, "删除记录id="+id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			close();
		}
		return deleteRows>0;
		
	}
	

}
