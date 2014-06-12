package com.famo.twentyonedays.ui;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.PoiInfo.POITYPE;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.famo.twentyonedays.R;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MapViewActivity extends BaseActivity {
    private static final String TAG = "MapViewActivity";
    private MapView mapView;
    private BaiduMap map;
    private Marker marker0;
    private PoiSearch poiSearch;
    private BusLineSearch mBusLineSearch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_mapview);
        
        mapView=(MapView) findViewById(R.id.bmapView);
        map = mapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//        map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        map.setTrafficEnabled(true);
        setCenter();
        markPoint();
//        markPolygonArea();
        markText();
        
        setMarkerListener();
        
        searchPoi();
        
        map.setOnMapClickListener(new MyOnMapClick());
    }
    private void setCenter() {
      //设定中心点坐标 
        LatLng cenpt = new LatLng(34.780614,113.688163); 
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
        .target(cenpt)
        .zoom(17)
        .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        map.setMapStatus(mMapStatusUpdate);
    }
    private void markPoint() {
        LatLng point=new LatLng(34.780414,113.688577);
        
        BitmapDescriptor bitmap=BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
      //构建MarkerOption，用于在地图上添加Marker  
        OverlayOptions option=new MarkerOptions()
        .position(point)
        .icon(bitmap);
        
        //在地图上添加Marker并显示
        marker0=(Marker)map.addOverlay(option);
    }
    
    private void markPolygonArea() {
        LatLng pt1 = new LatLng(34.781021, 113.689978);  //113.689978,34.781021
        LatLng pt2 = new LatLng(34.781185,113.690607);  //113.690607,34.781185
        LatLng pt3 = new LatLng(34.781118, 113.691191);  //113.691191,34.781118
        LatLng pt4 = new LatLng(34.780488, 113.691119);  //113.691119,34.780488
        LatLng pt5 = new LatLng(34.780406, 113.69058);  //113.69058,34.780406
        List<LatLng> pts = new ArrayList<LatLng>();  
        pts.add(pt1);  
        pts.add(pt2);  
        pts.add(pt3);  
        pts.add(pt4);  
        pts.add(pt5);  
        //构建用户绘制多边形的Option对象  
        OverlayOptions polygonOption = new PolygonOptions()  
            .points(pts)  
            .stroke(new Stroke(5, 0xAA00FF00))  
            .fillColor(0xAAFFFF00);  
        //在地图上添加多边形Option，用于显示  
        map.addOverlay(polygonOption);  
    }
    private void markText() {
        LatLng textPoint=new LatLng(34.780918, 113.690607);
        OverlayOptions option=new TextOptions()
        .bgColor(0xAAFFFF00)
        .text("21天效应")
        .fontColor(24)
        .fontColor(0xFFFF00FF)
        .rotate(30)
        .position(textPoint);
        map.addOverlay(option);
    }
    private void showInfoWindow(LatLng point) {
        Point p=map.getProjection().toScreenLocation(point);
        p.y-=47;
        LatLng llInfo=map.getProjection().fromScreenLocation(p);
        Button button=new Button(MapViewActivity.this);
        button.setBackgroundResource(R.drawable.popup);
        button.setText("infowindow");
        OnInfoWindowClickListener listener=new OnInfoWindowClickListener() {
            
            @Override
            public void onInfoWindowClick() {
                // TODO Auto-generated method stub
                
            }
        };
        InfoWindow window=new InfoWindow(button, llInfo, listener);
        map.showInfoWindow(window);
        
    }
    
    private void setMarkerListener() {
        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker==marker0) {
                    showInfoWindow(marker.getPosition());
                }
                return true;
            }
        });
    }
    
    private void searchPoi() {
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
        PoiCitySearchOption option=(new PoiCitySearchOption()).city("郑州").keyword("71");
        poiSearch.searchInCity(option);
        
    }
    
    private void searchBusLine(String uid) {
        Log.d(TAG, "查询公交路线");
        mBusLineSearch = BusLineSearch.newInstance();
        mBusLineSearch.setOnGetBusLineSearchResultListener(new MyOnGetBusLineResultListener());
        BusLineSearchOption option=new BusLineSearchOption().city("郑州").uid(uid);
        mBusLineSearch.searchBusLine(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        poiSearch.destroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    private List<PoiInfo> datas;
    private class MyPoiOverlay extends PoiOverlay {
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            Log.d(TAG, "index="+index);
            PoiInfo poiInfo = datas.get(index);
            Log.d(TAG, "poiInfo="+poiInfo.name);
            Log.d(TAG, "poiInfo.type="+poiInfo.type);
            return true;
        }
    }
    
    private class MyPoiSearchListener implements OnGetPoiSearchResultListener{

        @Override
        public void onGetPoiDetailResult(PoiDetailResult arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error == PoiResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(MapViewActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                return;
            }
            if (result.error == PoiResult.ERRORNO.NO_ERROR) {
                datas=result.getAllPoi();
                
//                map.clear();
                //创建PoiOverlay
                PoiOverlay overlay = new MyPoiOverlay(map);
                //设置overlay可以处理标注点击事件
                map.setOnMarkerClickListener(overlay);
                //设置PoiOverlay数据
                overlay.setData(result);
                //添加PoiOverlay到地图中
                overlay.addToMap();
                overlay.zoomToSpan();
                
                for(PoiInfo poi:datas) {
                    if(poi.type==PoiInfo.POITYPE.BUS_LINE||poi.type==PoiInfo.POITYPE.SUBWAY_LINE) {
                        searchBusLine(poi.uid);
                    }
                }
                return;
            }
        }
        
    }
    
    private class MyOnGetBusLineResultListener implements OnGetBusLineSearchResultListener{

        @Override
        public void onGetBusLineResult(BusLineResult result) {
            if(result==null||result.error!=SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MapViewActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                return;
            }
//            map.clear();
            
            BusLineOverlay overlay=new BusLineOverlay(map);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            Toast.makeText(MapViewActivity.this, result.getBusLineName(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, "公交线路查询结果="+result.getBusLineName());
            Log.d(TAG, "公交线路站="+result.getStations().get(0).getTitle());
        }
        
    }
    
    private class MyOnMapClick implements OnMapClickListener{

        @Override
        public void onMapClick(LatLng point) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public boolean onMapPoiClick(MapPoi poi) {
            Log.d(TAG, poi.getName());
            return true;
        }
        
    }


    
}
