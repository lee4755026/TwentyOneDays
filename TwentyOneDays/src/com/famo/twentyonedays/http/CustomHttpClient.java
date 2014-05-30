package com.famo.twentyonedays.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.famo.twentyonedays.BuildConfig;

import android.util.Log;

public class CustomHttpClient {
    private static final String CHARSET=HTTP.UTF_8;
    private static final String TAG = "CustomHttpClient";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static HttpClient customHttpClient;
    
    /**
     * 获取HttpClient实例
     * @return
     */
    public static synchronized HttpClient getHttpClient() {
        if(null==customHttpClient) {
            HttpParams params=new BasicHttpParams();
            
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83)" + 
                "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            //从连接池获取连接的超时时间
            ConnManagerParams.setTimeout(params, 1000);
            //连接超时
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            //请求超时
            HttpConnectionParams.setSoTimeout(params, 4000);
            //设置HttpClient支持Http和Https两种模式
            SchemeRegistry registry=new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
         // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager connectionManager=new ThreadSafeClientConnManager(params, registry);
            customHttpClient=new DefaultHttpClient(connectionManager, params);
        }
        return customHttpClient;
    }
    
    /**
     * POST请求
     * @param url
     * @param params
     * @return
     */
    public static String post(String url,NameValuePair... params ) {
        try {
            List<NameValuePair> formatParams=stripNulls(params);
         
            UrlEncodedFormEntity entity=new UrlEncodedFormEntity(formatParams, CHARSET);
            HttpPost request=new HttpPost(url);
            request.setEntity(entity);
            
            HttpClient client=getHttpClient();
            HttpResponse response=client.execute(request);
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity=response.getEntity();
            return resEntity==null?null:EntityUtils.toString(resEntity, CHARSET);
            
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            throw new RuntimeException("连接失败");
        }
    }
    
    /**
     * GET请求
     * @param url
     * @param params
     * @return
     */
    public static String get(String url,NameValuePair... params) {
        try {
            String query=URLEncodedUtils.format(stripNulls(params), CHARSET);
            HttpGet request=new HttpGet(url+"?"+query);
            
            if(DEBUG)Log.d(TAG, request.getURI().getPath());
//            request.addHeader( "User-Agent", clientVersion );
            // HTTP/1.1
            request.addHeader( "Cache-Control", "no-cache" );
            // HTTP/1.0
            request.addHeader( "Pragma", "no-cache" );  
            
            HttpClient client=getHttpClient();
            HttpResponse response=client.execute(request);
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity=response.getEntity();
            return resEntity==null?null:EntityUtils.toString(resEntity, CHARSET);
        } catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
//            throw new RuntimeException("连接失败");
            return null;
        }
    }
    
    private static List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if(nameValuePairs==null) return params;
        for ( int i = 0; i < nameValuePairs.length; i++ ) {
            NameValuePair param = nameValuePairs[ i ];
            if ( param.getValue() != null ) {
                params.add( param );
            }
        }
        return params;
     }
}
