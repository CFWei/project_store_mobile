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
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	public static String ServerURL="http://192.168.0.100/";
	private String Store_ID,Store_passwd; 
	SharedPreferences account_settings;
	Button LoginButton,ClearButton;
	private Thread mthread;
	private Handler mhandler;
	public ArrayList<HashMap<String,String>> item_list=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        account_settings = getSharedPreferences ("ACCOUNT", 0);
        Store_ID=account_settings.getString("Store_ID","");
        Store_passwd=account_settings.getString("Store_passwd","");
    	final EditText ID=(EditText)findViewById(R.id.IDeditText);
    	final EditText Passwd=(EditText)findViewById(R.id.PasswordeditText);
        
    	ID.setText(Store_ID);
    	Passwd.setText(Store_passwd);
        
    	mhandler=new Handler(){
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			switch (msg.what)
    			{
    				case 1:
    					String MsgString = (String)msg.obj;
    					Toast.makeText(MainActivity.this, MsgString, Toast.LENGTH_LONG).show();
    					break;
    				
    		
    			}
    		}
    		
    	};
    	
        LoginButton=(Button)findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
			
				Store_ID=ID.getText().toString();
				Store_passwd=Passwd.getText().toString();
				
				//儲存preference
				
				SharedPreferences.Editor PE = account_settings.edit();
				PE.putString("Store_ID", Store_ID);
				PE.putString("Store_passwd", Store_passwd);
				PE.commit();
				
				mthread=new Thread(Login_Runnable);
				mthread.start();
				
			}
		});
        
        ClearButton=(Button)findViewById(R.id.ClearButton);
        ClearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) 
			{
				ID.setText("");
				Passwd.setText("");
				SharedPreferences.Editor PE = account_settings.edit();
				PE.putString("Store_ID", "");
				PE.putString("Store_passwd", "");
				PE.commit();
				
			
				
			}
		});
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    private Runnable Login_Runnable=new Runnable() {
		public void run() 
		{	
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("Store_ID",Store_ID));
				nameValuePairs.add(new BasicNameValuePair("Store_passwd",Store_passwd));
				String result=connect_to_server("project/store/login.php",nameValuePairs);
				if(result.equals("fail"))
				{	
					String errormessage="Login Fail";
					Message m=mhandler.obtainMessage(1,errormessage);
					mhandler.sendMessage(m);
					
				}
				else
				{
					
					Message m=mhandler.obtainMessage(1,result);
					mhandler.sendMessage(m);
					
					Intent intent = new Intent();
					intent.setClass(MainActivity.this,ManageActivity.class);
					
					Bundle bundle=new Bundle();
					bundle.putString("SerialNumbers",result);
					intent.putExtras(bundle);

					startActivity(intent);
					
					MainActivity.this.finish();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
    	HttpPost httppost = new HttpPost(ServerURL+program);
    	
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
