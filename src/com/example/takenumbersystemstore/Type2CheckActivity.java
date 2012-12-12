package com.example.takenumbersystemstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Type2CheckActivity extends Activity {
	String SerialNumbers;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSerialNumber();
        new CheckStoreState().execute();

     
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type2_check, menu);
        return true;
    }
    
    public void getSerialNumber(){
    	 Intent intent=this.getIntent();
         Bundle bundle=intent.getExtras();
         SerialNumbers=bundle.getString("SerialNumbers");
    	
    }
    
    public void startType2Activity(){

    	Intent intent = new Intent();
    	Bundle bundle=new Bundle();
		
		intent.setClass(Type2CheckActivity.this,Type2MainActivity.class);
		bundle.putString("SerialNumbers",SerialNumbers);
		intent.putExtras(bundle);
		startActivity(intent);
        
		finish();
    	
    	
    }
    
    class CheckStoreState extends AsyncTask<Void, Void, String>{
    	
    	
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			String result=connect_to_server("project/store/Type2/checkStoreState.php",nameValuePairs);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if(result.equals("1")){
				Toast.makeText(Type2CheckActivity.this, "YA", Toast.LENGTH_SHORT).show();
				startType2Activity();
			}
			else{
				 setContentView(R.layout.activity_type2_check);
				 setButton();
				
			}
			super.onPostExecute(result);
		}
    	
		
    	
    }
    
    class startBusiness extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			String result=connect_to_server("project/store/Type2/StartBusiness.php",nameValuePairs);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result.equals("-1")){
				Toast.makeText(Type2CheckActivity.this, "無法取得SerialNumbers", Toast.LENGTH_SHORT).show();
			}
			else if(result.equals("-2")){
				Toast.makeText(Type2CheckActivity.this, "已經開始營業", Toast.LENGTH_SHORT).show();
				startType2Activity();
				
			}
			else if(result.equals("1")){
				Toast.makeText(Type2CheckActivity.this, "開始營業成功", Toast.LENGTH_SHORT).show();
				startType2Activity();
			
			}
			else{
				Toast.makeText(Type2CheckActivity.this, "不明原因的錯誤", Toast.LENGTH_SHORT).show();
				
			}
			
			
		}
    	
    	
    }
    
    public void setButton(){
    	 Button LogoutButton=(Button)findViewById(R.id.Logout);
         LogoutButton.setOnClickListener(new Button.OnClickListener() {
 			
 			public void onClick(View v) {
 				SharedPreferences account_settings = getSharedPreferences ("ACCOUNT", 0);
				SharedPreferences.Editor PE = account_settings.edit();
				PE.putString("AutoLogin","0");
				PE.commit();
				
				Intent intent = new Intent();
				intent.setClass(Type2CheckActivity.this,MainActivity.class);
				startActivity(intent);
				Type2CheckActivity.this.finish();
 			}
 		});
        
         
        Button settingItemListButton=(Button)findViewById(R.id.SettingItemList);
        settingItemListButton.setOnClickListener(new Button.OnClickListener() {
 			
 			public void onClick(View v) {
 				
 				Intent intent = new Intent();
 				intent.setClass(Type2CheckActivity.this,Type2ItemList.class);
 				
 				Bundle bundle=new Bundle();
				bundle.putString("SerialNumbers",SerialNumbers);
				intent.putExtras(bundle);
				
 				startActivity(intent);
 				
 			}
 		});
        
        
        Button EditStoreInformationButton=(Button)findViewById(R.id.EditStoreInformation); 
        EditStoreInformationButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Type2CheckActivity.this,modify_store_information.class);
				
				Bundle bundle=new Bundle();
				bundle.putString("SerialNumbers",SerialNumbers);
				intent.putExtras(bundle);
				
				startActivity(intent);
				
			}
		}); 
         
    	
        Button startBusiness=(Button)findViewById(R.id.startBusiness);
        startBusiness.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new startBusiness().execute();
			}
		});
        
    	
    }
    
    
    
	public String connect_to_server(String program,ArrayList<NameValuePair> nameValuePairs) 
    {	
    	//建立一個httpclient
    	HttpClient httpclient = new DefaultHttpClient();
    	//設定httppost的網址
    	HttpPost httppost = new HttpPost(MainActivity.ServerURL+program);
    	String result="";
    	//加入參數
    	
			try {
				if(nameValuePairs!=null)
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
				result = sb.toString();
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
		//發出httppost要求並接收回傳轉成字串
		
		
		
		return result;
    }
    
}
