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

public class Type2SettingPage extends Activity {
	String ReturnString="Return";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type2_setting_page);
        
        Button EditStoreInformationButton=(Button)findViewById(R.id.EditStoreInformation); 
        EditStoreInformationButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Type2SettingPage.this,modify_store_information.class);
				
				Bundle bundle=new Bundle();
				bundle.putString("SerialNumbers",Type2MainActivity.SerialNumbers);
				intent.putExtras(bundle);
				
				startActivity(intent);
				
			}
		});
        
        Button settingItemListButton=(Button)findViewById(R.id.SettingItemList);
        settingItemListButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(Type2SettingPage.this,Type2ItemList.class);
				
				Bundle bundle=new Bundle();
				bundle.putString("SerialNumbers",Type2MainActivity.SerialNumbers);
				intent.putExtras(bundle);
				
				startActivity(intent);
				
			}
		});
        
        Button LogoutButton=(Button)findViewById(R.id.Logout);
        LogoutButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				ReturnString="Logout";
				Type2SettingPage.this.finish();
				
				
			}
		});
        
        Button EndBusiness=(Button)findViewById(R.id.EndBusiness);
        EndBusiness.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new EndBusiness().execute();
			}
		});
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type2_setting_page, menu);
        return true;
    }

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
		Intent i=new Intent();
		Bundle b=new Bundle();
		b.putString("Button", ReturnString);
		i.putExtras(b);
		
		setResult(RESULT_OK, i);
		super.finish();
	}
    
	class EndBusiness extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",Type2MainActivity.SerialNumbers));
			String result=connect_to_server("project/store/Type2/EndBusiness.php",nameValuePairs);
			return result;
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result.equals("1")){
				ReturnString="EndBusiness";
				Type2SettingPage.this.finish();
				
				
			}
			else{
				
				Toast.makeText(Type2SettingPage.this, result, Toast.LENGTH_SHORT).show();
			}
		}
		
		
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
