package com.famo.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;

import android.graphics.Color;
import android.test.AndroidTestCase;
import android.util.Log;

public class DataBaseManagerTest extends AndroidTestCase {
	private static final String TAG = "DataBaseManagerTest";
	DataBaseManager manager=null;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager=new DataBaseManager(getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager=null;
	}
	
	public void testGetPlanEntries(){
		List<PlanEntry>list=manager.getPlanEntries();
		Log.d(TAG, "###"+Arrays.toString(list.toArray()));
	}
	public void testInsert(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendar=Calendar.getInstance();
		PlanEntry entry=new PlanEntry(0, "一个新计划");
		entry.content="习惯决定性格，性格决定命运。";
		entry.createTime=sdf.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		entry.startDate=sdf.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 20);
		entry.endDate=sdf.format(calendar.getTime());
		entry.reminderTime="21:00";
		entry.color=Color.RED;
		
		Log.d(TAG,"###"+entry);
		manager.insert(entry);
	}

}
