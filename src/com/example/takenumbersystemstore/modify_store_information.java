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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class modify_store_information extends Activity {
	private String SerialNumbers;
	private Thread mthread;
	private static Handler mhandler;
	private static double lon=new Double(0);
	private static double lat=new Double(0);
	private static int[] check_modify=new int [5];
	public static ArrayList<HashMap<String,String>> store_information=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_store_information);
        
        Intent thisIntent = this.getIntent();
        Bundle bundle=thisIntent.getExtras();
        SerialNumbers=bundle.getString("SerialNumbers");
        
        for(int i=0;i<check_modify.length;i++)
        {
        	check_modify[i]=0;
        	
        }
        
        
        mhandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what)
    			{
					case 1:
						EditText storename=(EditText)findViewById(R.id.store_name_value);
						storename.setText(store_information.get(0).get("StoreName"));
						storename.addTextChangedListener(new TextWatcher() {
							
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								// TODO Auto-generated method stub
								
							}
							
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								// TODO Auto-generated method stub
								
							}
							
							public void afterTextChanged(Editable s) {
								TextView name_hint=(TextView)findViewById(R.id.name_hint);
								if(!s.toString().equals(store_information.get(0).get("StoreName")))
									name_hint.setText("*");
								else
									name_hint.setText("");
							}
						} );
					
						EditText storetel=(EditText)findViewById(R.id.store_telephone_value);
						storetel.setText(store_information.get(0).get("StoreTelephone"));
						storetel.addTextChangedListener(new TextWatcher() {
							
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								// TODO Auto-generated method stub
								
							}
							
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								// TODO Auto-generated method stub
								
							}
							
							public void afterTextChanged(Editable s) {
								TextView tel_hint=(TextView)findViewById(R.id.tel_hint);
								if(!s.toString().equals(store_information.get(0).get("StoreTelephone")))
									tel_hint.setText("*");
								else
									tel_hint.setText("");
							}
						});
						
						EditText storeAdd=(EditText)findViewById(R.id.store_address_value);
						storeAdd.setText(store_information.get(0).get("StoreAddress"));
						storeAdd.addTextChangedListener(new TextWatcher() {
							
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								// TODO Auto-generated method stub
								
							}
							
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								// TODO Auto-generated method stub
								
							}
							
							public void afterTextChanged(Editable s) {
								TextView add_hint=(TextView)findViewById(R.id.add_hint);
								if(!s.toString().equals(store_information.get(0).get("StoreAddress")))
									add_hint.setText("*");
								else
									add_hint.setText("");
							}
						});
						
						TextView longitude=(TextView)findViewById(R.id.longitude_value);
						longitude.setText(store_information.get(0).get("GPS_Longitude"));
						longitude.addTextChangedListener(new TextWatcher() {
							
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								// TODO Auto-generated method stub
								
							}
							
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								// TODO Auto-generated method stub
								
							}
							
							public void afterTextChanged(Editable s) {
								TextView lon_hint=(TextView)findViewById(R.id.lon_hint);
								if(!s.toString().equals(store_information.get(0).get("GPS_Longitude")))
									lon_hint.setText("*");
								else
									lon_hint.setText("");
							}
						});
						
						TextView latitude=(TextView)findViewById(R.id.latitude_value);
						latitude.setText(store_information.get(0).get("GPS_Latitude"));
						latitude.addTextChangedListener(new TextWatcher() {
							
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								// TODO Auto-generated method stub
								
							}
							
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								// TODO Auto-generated method stub
								
							}
							
							public void afterTextChanged(Editable s) {
								TextView lat_hint=(TextView)findViewById(R.id.lat_hint);
								if(!s.toString().equals(store_information.get(0).get("GPS_Latitude")))
									lat_hint.setText("*");
								else
									lat_hint.setText("");
							}
						});
						
						break;
					case 2:
						TextView lon_value=(TextView)findViewById(R.id.longitude_value);
						lon_value.setText(String.valueOf(lon));
						
						TextView lat_value=(TextView)findViewById(R.id.latitude_value);
						lat_value.setText(String.valueOf(lat));
						
						break;
					case 3:
						Toast.makeText(modify_store_information.this, "商家資訊修改成功",Toast.LENGTH_SHORT).show();
						modify_store_information.this.finish();
						break;
    			}
			}
        	
        	
        };
        
        Button convert_button=(Button)findViewById(R.id.convert);
        convert_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				EditText storeAdd=(EditText)findViewById(R.id.store_address_value);
				final String add=storeAdd.getText().toString();
				Thread codeaddress_thread=new Thread(new Runnable() {

					public void run() {
						getLocationInfo(add);
					}
				});
				codeaddress_thread.start();
				
				
			}
	
			
		});
        
        Button submit =(Button)findViewById(R.id.submit_button);
        submit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Thread send_modify=new Thread(new Runnable() {
					
					public void run() {
						
						
						try {
							EditText storename=(EditText)findViewById(R.id.store_name_value);
							String storename_value=storename.getText().toString();
							
							EditText storetel=(EditText)findViewById(R.id.store_telephone_value);
							String storetel_value=storetel.getText().toString();
							
							EditText storeAdd=(EditText)findViewById(R.id.store_address_value);
							String storeAdd_value=storeAdd.getText().toString();
							
							TextView lon=(TextView)findViewById(R.id.longitude_value);
							String lon_value=lon.getText().toString();
							
							TextView lat=(TextView)findViewById(R.id.latitude_value);
							String lat_value=lat.getText().toString();
							
							ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
							nameValuePairs.add(new BasicNameValuePair("StoreName",storename_value));
							nameValuePairs.add(new BasicNameValuePair("StoreAddress",storeAdd_value));
							nameValuePairs.add(new BasicNameValuePair("StoreTelephone",storetel_value));
							nameValuePairs.add(new BasicNameValuePair("GPS_Longitude",lon_value));
							nameValuePairs.add(new BasicNameValuePair("GPS_Latitude",lat_value));
							String result=connect_to_server("/project/store/modify_store_information.php",nameValuePairs);
							if(result.equals("successful"))
								{
								 	Message m=mhandler.obtainMessage(3);
								 	mhandler.sendMessage(m);
								}
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}
				});
				send_modify.start();
				
			}
		});
        
        
		mthread=new Thread(get_store_information);
		mthread.start();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_modify_store_information, menu);
        return true;
    }
    
    
    private Runnable get_store_information=new Runnable() {
		
		public void run() {
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				String result=connect_to_server("/project/store/get_store_information.php",nameValuePairs);
				Log.v("debug", result);
				String key[]={"StoreName","StoreAddress","StoreTelephone","GPS_Longitude","GPS_Latitude"};
				store_information=json_deconde(result,key);
				
				Message m=mhandler.obtainMessage(1);
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
    
    public String connect_to_server(String program,ArrayList<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException
    {	
    	//建立一個httpclient
    	HttpClient httpclient = new DefaultHttpClient();
    	//設定httppost的網址
    	HttpPost httppost = new HttpPost(MainActivity.ServerURL+program);
    	
    	//加入參數
    	if(nameValuePairs!=null)
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
    	
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
    
	public static JSONObject getLocationInfo(String address)
	{
		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address="+ address + "ka&sensor=false" );
		HttpClient client = new DefaultHttpClient();  
		HttpResponse response; 
		StringBuilder stringBuilder = new StringBuilder();  
		try{
			response = client.execute(httpGet);  
			HttpEntity entity = response.getEntity();  
			InputStream stream = entity.getContent();
			int b; 
			while ((b = stream.read()) != - 1 ) 
			{
				 stringBuilder.append(( char ) b);  
			}
			
		}
		 catch (ClientProtocolException e) {}
		 catch (IOException e) {}
		 JSONObject jsonObject = new JSONObject();  
		 try 
		 {
			 jsonObject = new JSONObject(stringBuilder.toString());  
			 lon=((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject( "geometry" ).getJSONObject( "location" ).getDouble( "lng" );
			 lat=((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject( "geometry" ).getJSONObject( "location" ).getDouble( "lat" );
			 Message m=mhandler.obtainMessage(2);
			 mhandler.sendMessage(m);
		 }
		 catch (JSONException e) { 
			 e.printStackTrace(); 
		 }
		 return jsonObject; 

	}
	
    
}
