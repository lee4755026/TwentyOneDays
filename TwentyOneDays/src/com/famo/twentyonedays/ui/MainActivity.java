package com.famo.twentyonedays.ui;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.famo.twentyonedays.BuildConfig;
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
    private static final int REQUEST_CODE_ADD = 0;
    private static final int REQUEST_CODE_OPENGPS = 1;
	private View progress;
	private View empty;
	private ListViewCustom listView;
	private Button buttonAdd;
	private TextView week;
	private PlansAdapter adapter;
	private List<PlanEntry> dataList;
	private DataBaseManager manager;
	private ActionBar actionBar;
    private LocationManager locationManager;
    private long firstTime;
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
		
        disableHardWareMenu();
        
        openGPSSetting();
	}
	
    /**
     * 禁用menu键
     */
    private void disableHardWareMenu() {
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
				Intent intent=new Intent(MainActivity.this,AdditionActivity.class);
				startActivityForResult(intent, 0);
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
	public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 1000) {
            Toast.makeText(this, "再按一次返回键关闭程序", Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();

        } else {
            super.onBackPressed();
            getMyApplication().exit();
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    switch(requestCode) {
	    case REQUEST_CODE_ADD:
	        stopService(new Intent(this, ReminderService.class));
	        startService(new Intent(this, ReminderService.class));
	        break;
	    case REQUEST_CODE_OPENGPS:
	        openGPSSetting();
	        break;
	    }
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
	    
	    menu.findItem(R.id.menu_map).setVisible(BuildConfig.DEBUG);
	    
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
            Intent intent = new Intent(MainActivity.this, AdditionActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
            break;
        case R.id.menu_about:
            Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intentAbout);
            break;
        case R.id.menu_map:
            Intent intentMap = new Intent(MainActivity.this, MapViewActivity.class);
            startActivity(intentMap);
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

	private void openGPSSetting() {
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	        Log.d(TAG, "GPS模块正常");
	        getLocation();
	    }else {
	        Intent intentGps = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
            
            // 设置完成后返回到原来的界面
            startActivityForResult( intentGps, REQUEST_CODE_OPENGPS ); 
	    }
	}
	
	/**
	 * 获取坐标
	 */
	private void getLocation() {
	    String provider=locationManager.getBestProvider(getCriteria(), true);
	    Location location=locationManager.getLastKnownLocation(provider);
//	    Log.d(TAG, String.valueOf(location));
	    printLocationInfo(location);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, provider+"状态变化");
            }
            
            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, provider+"可用");
                Log.d(TAG, locationManager.getLastKnownLocation(provider).toString());
                printLocationInfo(locationManager.getLastKnownLocation(provider));
            }
            
            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, provider+"不可用");
            }
            
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "位置变化，"+location.toString());
                printLocationInfo(location);
            }
        });
	}
	
	private Criteria getCriteria() {
	    //设置位置查询条件，通过criteria返回符合条件的provider,有可能是wifi provider,也有可能是gps provider  
        Criteria criteria =new Criteria(); //创建一个Criteria对象         
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); //设置精度,模糊模式,对于DTV地区定位足够了；ACCURACY_FINE,精确模式          
        criteria.setAltitudeRequired(false); //设置是否需要返回海拔信息,不要求海拔          
        criteria.setBearingRequired(false); //设置是否需要返回方位信息，不要求方位          
        criteria.setCostAllowed(true); //设置是否允许付费服          
        criteria.setPowerRequirement(Criteria.POWER_LOW); //设置电量消耗等级          
        criteria.setSpeedRequired(false); //设置是否需要返回速度信息  
        return criteria;
	}
	
	private void printLocationInfo(Location location) {
	    if(location==null) return;
	    String info="location,经度="+location.getLongitude()
	            +",纬度="+location.getLatitude()
	            +",高度="+location.getAltitude()
	            +",精度="+location.getAccuracy()
	            +",方向="+location.getBearing()
	            +",速度="+location.getSpeed()
	            +",时间="+location.getTime();
	    Log.d(TAG, info);
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
