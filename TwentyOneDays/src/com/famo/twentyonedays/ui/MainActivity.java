package com.famo.twentyonedays.ui;

import java.util.ArrayList;
import java.util.List;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.R.layout;
import com.famo.twentyonedays.adapter.PlansAdapter;
import com.famo.twentyonedays.model.PlanEntry;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends Activity {
	private View progress;
	private View empty;
	private ListView listView;
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
		listView=(ListView) findViewById(android.R.id.list);
	}
	private void bindEvents() {
		// TODO Auto-generated method stub
		
	}
	
	private void bindData(){
		List<PlanEntry> dataList=new ArrayList<PlanEntry>();
		dataList.add(new PlanEntry(1,"每天读书半小时"));
		dataList.add(new PlanEntry(2,"每天准备七点起床"));
		dataList.add(new PlanEntry(2,"每天只上网三小时"));
		dataList.add(new PlanEntry(2,"每天少看十分钟电视"));
		
		PlansAdapter adapter=new PlansAdapter(this, dataList);
		listView.setAdapter(adapter);
		progress.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	}

	

}
