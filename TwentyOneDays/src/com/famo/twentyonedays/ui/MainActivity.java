package com.famo.twentyonedays.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import com.famo.twentyonedays.R;
import com.famo.twentyonedays.adapter.PlansAdapter;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.ui.widget.ListViewCustom;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	public static final String PLAN_ID = "planId";
	public static final String PLAN_TITLE = "planTitle";
	private View progress;
	private View empty;
	private ListViewCustom listView;
	private Button buttonAdd;
	private PlansAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		bindEvents();
		bindData();
	}
	private void findViews() {
		progress=findViewById(android.R.id.progress);
		empty=findViewById(android.R.id.empty);
		listView=(ListViewCustom) findViewById(android.R.id.list);
		buttonAdd=(Button) findViewById(R.id.add_button);
	}
	private void bindEvents() {
		buttonAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainActivity.this,DetailActivity.class	);
				startActivity(intent);
			}
		});
	}
	
	private void bindData(){
		List<PlanEntry> dataList=new ArrayList<PlanEntry>();
		dataList.add(new PlanEntry(1,"每天读书半小时"));
		dataList.add(new PlanEntry(2,"每天准备七点起床"));
		dataList.add(new PlanEntry(3,"每天只上网三小时"));
		dataList.add(new PlanEntry(4,"每天少看十分钟电视"));
		
		adapter = new PlansAdapter(this, dataList);
		listView.setAdapter(adapter);
		progress.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	}
	
	


	

}
