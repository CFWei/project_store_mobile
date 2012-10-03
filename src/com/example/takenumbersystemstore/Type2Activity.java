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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Type2Activity extends Activity {
	public String SerialNumbers;
	public ArrayList<HashMap<String,String>> CustomList=null;
	public ArrayList<HashMap<String,String>> CustomItemList=null;
	private Handler mhandler;
	private CustomListItemAdapter CLIA;
	private CustomItemAdapter CIA;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type2);
        
        //從Bundle取得SerialNumbers
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
    					Toast.makeText(Type2Activity.this, MsgString, Toast.LENGTH_SHORT).show();
    					break;
    				case 2:
    					CLIA=new CustomListItemAdapter(Type2Activity.this, CustomList);
    					ListView CustomListView=(ListView)findViewById(R.id.CustomListView);
    					CustomListView.setAdapter(CLIA);
    					CustomListView.setOnItemClickListener(new ListView.OnItemClickListener() 
    					{
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) 
							{	
								String CustomNum=CustomList.get(arg2).get("number");
								GetCustomItem GCI=new GetCustomItem(CustomNum);
								Thread GCIThread=new Thread(GCI);
								GCIThread.start();
							}
    						
    						
						});
    					
    					break;
    				case 3:
    					CIA=new CustomItemAdapter(Type2Activity.this, CustomItemList);
    					ListView CustomItemListView=(ListView)findViewById(R.id.CustomItemList);
    					CustomItemListView.setAdapter(CIA);
    					break;
    					
    			}
    		}
    		
    	};
        
        
        
        
        
        Thread GCLThread=new Thread(GetCustomList);
        GCLThread.start();

    }
    
    
    Runnable GetCustomList=new Runnable() {
		
		public void run() 
		{
			try {
					
					ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
					String result=connect_to_server("project/store/Type2/GetCustomList.php",nameValuePairs);
					if(result.equals("-1"))
					{
						
						
					}
					else
					{	
						
						String key[]={"number"};
						CustomList=json_deconde(result, key);
						Message m=mhandler.obtainMessage(2,result);
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
	
	class GetCustomItem implements Runnable 
	{
		String CustomNum;
		public GetCustomItem(String CustomNum)
		{
			this.CustomNum=CustomNum;
			
		}
		
		public void run() 
		{
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			nameValuePairs.add(new BasicNameValuePair("CustomNumber",CustomNum));
			try {
				String result=connect_to_server("project/store/Type2/GetCustomItem.php",nameValuePairs);
				String key[]={"ItemName","NeedValue","Price"};
				CustomItemList=json_deconde(result, key);
				
				Message m=mhandler.obtainMessage(3,result);
				mhandler.sendMessage(m);
				
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
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type2, menu);
        return true;
    }
    
    
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
