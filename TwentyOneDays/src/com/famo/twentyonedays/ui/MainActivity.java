package com.famo.twentyonedays.ui;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.adapter.PlansAdapter;
import com.famo.twentyonedays.datacenter.manager.DataBaseManager;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.services.ReminderService;
import com.famo.twentyonedays.ui.widget.ListViewCustom;
import com.famo.twentyonedays.utils.Tools;

public class MainActivity extends BaseActivity {
	private static final String ADD = "add";
    protected static final String TAG = "MainActivity";
	public static final String PLAN_ID = "planId";
	public static final String PLAN_TITLE = "planTitle";
	public static final String PLAN_CONTENT="planContent";
    private static final CharSequence ABOUT = "feedback";
	private View progress;
	private View empty;
	private ListViewCustom listView;
	private Button buttonAdd;
	private TextView week;
	private PlansAdapter adapter;
	private List<PlanEntry> dataList;
	private DataBaseManager manager;
	private ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		bindEvents();
		
		manager=new DataBaseManager(MainActivity.this);
		actionBar=getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		
		startService(new Intent(this,ReminderService.class));
		logger=Logger.getLogger(MainActivity.class);
		logger.info("mainactivity 启动");
		
        try {
            ViewConfiguration mconfig = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(mconfig, false);
            }
        } catch (Exception ex) {
        }
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
		String weekDay=Tools.currentWeekDay();
		week.setText(weekDay);
		adapter = new PlansAdapter(this, dataList);
		listView.setAdapter(adapter);
		
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
//	    MenuItemCompat.setShowAsAction( 
//          menu.add(ADD) 
//          .setIcon(R.drawable.add),
////          .setTitle("添加"),
//          MenuItemCompat.SHOW_AS_ACTION_IF_ROOM); 
//	    MenuItemCompat.setShowAsAction( 
//	        menu.add(ABOUT) 
//	        .setIcon(R.drawable.abc_ic_go_search_api_holo_light)
//          .setTitle("关于"),
//	        MenuItemCompat.SHOW_AS_ACTION_IF_ROOM); 
	    MenuInflater inflater=getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	     
	    
        return true;
    }
	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_add:
            Intent intent=new Intent(MainActivity.this,AdditionActivity.class);
            startActivity(intent);
            break;
        case R.id.menu_about:
            Intent intentAbout=new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intentAbout);
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
	protected void onResume() {
		super.onResume();
		new LoadPlanData().execute();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		stopService(new Intent(this, ReminderService.class));
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
