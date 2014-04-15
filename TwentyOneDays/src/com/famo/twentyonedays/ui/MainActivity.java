package com.famo.twentyonedays.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.adapter.PlansAdapter;
import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.services.ReminderService;
import com.famo.twentyonedays.ui.widget.ListViewCustom;
import com.famo.twentyonedays.utils.Tools;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	public static final String PLAN_ID = "planId";
	public static final String PLAN_TITLE = "planTitle";
	public static final String PLAN_CONTENT="planContent";
	private View progress;
	private View empty;
	private ListViewCustom listView;
	private Button buttonAdd;
	private TextView week;
	private PlansAdapter adapter;
	private List<PlanEntry> dataList;
	private DataBaseManager manager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		bindEvents();
		
		manager=new DataBaseManager(MainActivity.this);
		
		startService(new Intent(this,ReminderService.class));
	}
	private void findViews() {
		progress=findViewById(android.R.id.progress);
		empty=findViewById(android.R.id.empty);
		listView=(ListViewCustom) findViewById(android.R.id.list);
		buttonAdd=(Button) findViewById(R.id.add_button);
		week=(TextView) findViewById(R.id.week);
	}
	private void bindEvents() {
		buttonAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainActivity.this,AdditionActivity.class	);
				startActivity(intent);
			}
		});
		listView.setOnItemDeleteClickListener(new ListViewCustom.OnItemDeleteClickListener() {
			
			@Override
			public void onDelete( int position) {
				final int index=position;
	            new AlertDialog.Builder(MainActivity.this)
	            .setTitle("提示")
	            .setMessage("确定要删除吗?")
	            .setPositiveButton("是",new DialogInterface.OnClickListener() {
	            	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						PlanEntry delEntry=dataList.remove(index);
						manager.delete(delEntry.id);						
			            adapter.notifyDataSetChanged();	
					}
				})
				.setNegativeButton("否", null)
				.create().show();				
			}
		});
	}
	
	private void bindData(){
//		dataList = new ArrayList<PlanEntry>();
//		dataList.add(new PlanEntry(1,"每天读书半小时"));
//		dataList.add(new PlanEntry(2,"每天准备七点起床"));
//		dataList.add(new PlanEntry(3,"每天只上网三小时"));
//		dataList.add(new PlanEntry(4,"每天少看十分钟电视"));
		String weekDay=Tools.currentWeekDay();
		week.setText(weekDay);
		adapter = new PlansAdapter(this, dataList);
		listView.setAdapter(adapter);
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		new LoadPlanData().execute();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this, ReminderService.class));
	}




	private class LoadPlanData extends AsyncTask<Void, Void,Boolean>{
		@Override
		protected void onPreExecute() {
			progress.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			empty.setVisibility(View.GONE);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			dataList=manager.getPlanEntries();
			
			return !dataList.isEmpty();
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				progress.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				bindData();
			}else{
				progress.setVisibility(View.GONE);
				listView.setVisibility(View.GONE);
				empty.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	


	

}
