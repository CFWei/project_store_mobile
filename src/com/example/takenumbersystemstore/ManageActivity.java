package com.example.takenumbersystemstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ManageActivity extends Activity {
	String SerialNumbers;
	private Thread mthread;
	private Handler mhandler;
	public ArrayList<HashMap<String,String>> item_list=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        
       Intent thisIntent = this.getIntent();
       Bundle bundle=thisIntent.getExtras();
       SerialNumbers=bundle.getString("SerialNumbers");
        
        mhandler=new Handler(){
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			switch (msg.what)
    			{
    				case 1:
    					String MsgString = (String)msg.obj;
    					Toast.makeText(ManageActivity.this, MsgString, Toast.LENGTH_LONG).show();
    					break;
    		
    			}
    		}
    		
    	};
        
        
        
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mthread=new Thread(get_item_list);
		mthread.start();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_manage, menu);
        return true;
    }
    
    
	
	private Runnable get_item_list=new  Runnable() {
		public void run() 
		{	
	
			try {
					ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
					String result=connect_to_server("project/store/get_item_list.php",nameValuePairs);
					if(result.equals("fail"))
					{
						String errormessage="Get Item List Fail";
						Message m=mhandler.obtainMessage(1,errormessage);
						mhandler.sendMessage(m);
					}
					else
					{	
						if(result!="null"||result!="")
							{
								String key[]={"ID","Name","State","Value","Now_Value"};
								item_list=json_deconde(result,key);
							}
							
						Message m=mhandler.obtainMessage(1,result);
						mhandler.sendMessage(m);
						
					}	
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	
    public String connect_to_server(String program,ArrayList<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException
    {	
    	//建立一個httpclient
    	HttpClient httpclient = new DefaultHttpClient();
    	//設定httppost的網址
    	HttpPost httppost = new HttpPost(MainActivity.ServerURL+program);
    	
    	//加入參數
    	if(nameValuePairs!=null)
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	
		//發出httppost要求並接收回傳轉成字串
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8);
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) 
		{
			sb.append(line);
		}
		is.close();
		String result = sb.toString();
		
		return result;
    }
	
	public ArrayList<HashMap<String,String>> json_deconde(String jsonString,String[] key) throws JSONException
    {	
    	ArrayList<HashMap<String,String>> item = new ArrayList<HashMap<String,String>>();
    	JSONArray jArray = new JSONArray(jsonString);
    	for(int i = 0;i<jArray.length();i++)
		{	
    		 HashMap<String,String> temp = new HashMap<String,String>();
	     	 JSONObject json_data = jArray.getJSONObject(i); 
	     	 for(int j=0;j<key.length;j++)
	     	 	temp.put(key[j], json_data.getString(key[j]));
	     	 item.add(temp);
	     	 //Toast.makeText(this, json_data.getString(key[2]), Toast.LENGTH_SHORT).show();
		}

    	return item;
    }


}
