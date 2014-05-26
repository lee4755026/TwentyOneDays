package com.famo.test;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.famo.twentyonedays.R;
import com.famo.twentyonedays.utils.Tools;

import android.app.ProgressDialog;
import android.test.AndroidTestCase;
import android.util.Log;

public class VolleyTest extends AndroidTestCase {

	private static final String TAG = "VolleyTest";
	private RequestQueue requestQueue;

	public void testJsonObjectRequest(){
		 requestQueue = Volley.newRequestQueue(getContext());  
	        String JSONDataUrl = "http://218.28.201.229:81//ltw/SensorTypelist.asp?mineid=5022";  
//	        final ProgressDialog progressDialog = ProgressDialog.show(this, "This is title", "...Loading...");  
	        Log.d(TAG, "开始加载")	;
	  
	        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(  
	                Request.Method.GET,   
	                JSONDataUrl,    
	                null,  
	                new Response.Listener<JSONObject>() {  
	                    @Override  
	                    public void onResponse(JSONObject response) {  
	                        System.out.println("response="+response);  
	                        Log.d(TAG, "response="+response);
//	                        if (progressDialog.isShowing()&&progressDialog!=null) {  
//	                            progressDialog.dismiss();  
//	                        }  
	                    }  
	                },   
	                new Response.ErrorListener() {  
	                    @Override  
	                    public void onErrorResponse(VolleyError arg0) {  
	                           System.out.println("sorry,Error"); 
	                           Log.d(TAG, "sorry,Error");
	                    }  
	                });  
	        requestQueue.add(jsonObjectRequest); 
	        requestQueue.start();
	}
	
	public void testScreenShot() {
	    String format=getContext().getResources().getString(R.string.weibo_content_format);
	    Log.d(TAG, String.format(format, "测试",12,(int)(12f/21f*100)));
	}
}
