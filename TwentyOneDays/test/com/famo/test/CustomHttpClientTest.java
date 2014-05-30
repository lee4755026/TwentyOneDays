package com.famo.test;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.famo.twentyonedays.http.CustomHttpClient;

import android.test.AndroidTestCase;
import android.util.Log;

public class CustomHttpClientTest extends AndroidTestCase {
private static final String TAG = "CustomHttpClientTest";
//    public HttpClient client;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        client=CustomHttpClient.getHttpClient();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
//        client=null;
    }
    
    public void testPost() {
        
    }
    public void testGet() {
//        String url="http://twentydays.duapp.com/checkupdate";
        String url="http://192.168.12.19:18080/checkupdate";
        //username=test&password=hnsi
//        NameValuePair[] params=new BasicNameValuePair[] {
//                new BasicNameValuePair("username", "test"),
//                new BasicNameValuePair("password", "hnsi")
//        };
       String result= CustomHttpClient.get(url, null);
       Log.d(TAG, "test get="+result);
        
    }

}
