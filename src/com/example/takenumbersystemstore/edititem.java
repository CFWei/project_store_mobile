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
import org.apache.http.protocol.HTTP;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;

public class edititem extends Activity {
	private Thread mthread;
	private Handler mhandler;
	private String SerialNumbers;
	private String ID;
	private int modify_check[]=new int[1];
	 ArrayList<HashMap<String,String>> item_list;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edititem);
       
        for(int i=0;i<modify_check.length;i++)
        {
        	modify_check[i]=0;
        }
        
		Intent thisIntent = this.getIntent();
	    Bundle bundle=thisIntent.getExtras();
	    SerialNumbers=bundle.getString("SerialNumbers");
	    ID=bundle.getString("ID");
	    
	    final TextView hint=(TextView)findViewById(R.id.item_name_hint);
	    
	    mhandler=new Handler(){
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			switch (msg.what)
    			{
    				case 1:
    					EditText item_name=(EditText)findViewById(R.id.item_name_value);
    					item_name.setText(item_list.get(0).get("Name"));
    					item_name.addTextChangedListener(new TextWatcher() {
							
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								
								
							}
							
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								// TODO Auto-generated method stub
								
							}
							
							public void afterTextChanged(Editable s) {
								if(!s.toString().equals(item_list.get(0).get("name")))
									{
										hint.setText("*");
										modify_check[0]=1;
									}
								else
									{
										hint.setText("");
										modify_check[0]=0;
										
									}
							}
						});
    					break;
    				case 2:
    						Toast.makeText(edititem.this, "修改成功", Toast.LENGTH_SHORT).show();
    						edititem.this.finish();
    						break;
    				case 3:
    						Toast.makeText(edititem.this, "修改失敗", Toast.LENGTH_SHORT).show();
    						break;
    		
    			}
    		}
    		
    	};
	    
	    Button submit=(Button)findViewById(R.id.submit_edit);
	    submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) 
			{	
				Log.v("debug","thread start");
				Thread edit_item_thread=new Thread(edit_item);
				edit_item_thread.start();
				
				
			}
		});
    	
    	
    	
		mthread=new Thread(get_item_information);
		mthread.start();
        
    }
    
    private Runnable get_item_information=new Runnable() {
		
		public void run() 
		{
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("ID",ID));
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			try {
				
				String result=connect_to_server("project/store/get_item_information.php",nameValuePairs);
				if(result!=null)
					{	
						String key[]={"ID","Name","State","Value","Now_Value"};
						item_list=json_deconde(result,key);
						Message m=mhandler.obtainMessage(1);
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edititem, menu);
        return true;
    }
    
    private Runnable edit_item=new Runnable() {
		
		public void run() {
			
			try {
				EditText item_name=(EditText)findViewById(R.id.item_name_value);
				String item_name_value=item_name.getText().toString();
				Log.v("debug","start");
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("ID",ID));
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				nameValuePairs.add(new BasicNameValuePair("Name",item_name_value));
				
				String result = connect_to_server("project/store/edit_item_information.php",nameValuePairs);
				if(result.equals("success"))
				{
					
					Message m=mhandler.obtainMessage(2);
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
    
}
