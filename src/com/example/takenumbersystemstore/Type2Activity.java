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
import android.os.HandlerThread;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Type2Activity extends Activity {
	public static String SerialNumbers;
	public ArrayList<HashMap<String,String>> CustomList=null;
	public ArrayList<HashMap<String,String>> CustomItemList=null;
	private Handler mhandler;
	public CustomListItemAdapter CLIA;
	public CustomItemAdapter CIA;
	private String NowValue="0";
	private String Value="0";
	private int recordCustomList=0;
	private Handler handler;
	private int cc=0;
	private static String NowChooseCustom="0";
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
    					UpdateCustomList();
    					break;
    				case 3:
    					CIA=new CustomItemAdapter(Type2Activity.this, CustomItemList);
    					ListView CustomItemListView=(ListView)findViewById(R.id.CustomItemList);
    					CustomItemListView.setAdapter(CIA);
    					TextView NowChooseValue=(TextView)findViewById(R.id.NowChooseValueText);
    					NowChooseValue.setText(NowChooseCustom);
    					break;
    				case 4:
    					TextView NowValueTextView=(TextView)findViewById(R.id.NowValue);
    					NowValueTextView.setText(NowValue);
    					break;
    				case 5:
    					TextView ValueTextView=(TextView)findViewById(R.id.Value);
    					ValueTextView.setText(Value);
    					break;
    				case 6:
    					SharedPreferences account_settings = getSharedPreferences ("ACCOUNT", 0);
    					SharedPreferences.Editor PE = account_settings.edit();
    					PE.putString("AutoLogin","0");
    					PE.commit();
    					
    					Intent intent = new Intent();
    					intent.setClass(Type2Activity.this,MainActivity.class);
    					
    					startActivity(intent);
    					Type2Activity.this.finish();
    					break;
    					
    			}
    		}
    		
    	};
    	
    	
    	
    	
    	setAdapter();
    	
    	HandlerThread HT=new HandlerThread("123");
    	HT.start();
    	handler=new Handler(HT.getLooper());
    	
    	//設定CallTheNumber Button
    	Button CallTheNumber=(Button)findViewById(R.id.CallTheNumber);
    	CallTheNumber.setOnClickListener(new Button.OnClickListener() 
    	{
			
			public void onClick(View v) 
			{	
				
				Thread CallNumberThread=new Thread(CallNumber);
				CallNumberThread.start();
			}
		});

    	//設定logout button
    	Button logout=(Button)findViewById(R.id.Logout);
        logout.setOnClickListener(new OnClickListener() {

  		public void onClick(View v) 
  		{
			Message m=mhandler.obtainMessage(6);
			mhandler.sendMessage(m);
  			
  		}
  	});
    	
        Button SettingButton=(Button)findViewById(R.id.Setting);
        SettingButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				Bundle bundle=new Bundle();
				
				intent.setClass(Type2Activity.this,Type2SettingPage.class);
				
				//bundle.putString("SerialNumbers",StoreInformation.get(0).get("SerialNumbers"));
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
		});
        
    	
    	
        Thread UpdateValueThread=new Thread(UpdateValue);
        UpdateValueThread.start();
        
    }
    
    public void UpdateCustomList()
    {	

    	
    	CLIA.notifyDataSetChanged();
    	
    	
    	//更新WaitValue
    	int WaitValue=CustomList.size();
    	TextView WaitValueTextView=(TextView)findViewById(R.id.WaitValue);
    	WaitValueTextView.setText(String.valueOf(WaitValue));
 
    }
 
	public void setAdapter()
	{
		CustomList=new ArrayList<HashMap<String,String>>();
		
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
		
		
	}
    
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
			Type2Activity.NowChooseCustom=CustomNum;
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
	
	//更新list
	Runnable UpdateValue=new Runnable() 
	{
		
		public void run() 
		{
			
			try {
				
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				String result=connect_to_server("project/store/Type2/UpdateValue.php",nameValuePairs);
				
				
				
				String key[]={"NowValue","Value"};
				ArrayList<HashMap<String,String>> temp=json_deconde(result, key);
				
				String GetNowValue = temp.get(0).get("NowValue");
				String GetValue = temp.get(0).get("Value");
				
				
				//Log.v("debug", result);
				if(!GetValue.equals(Value))
				{	
					Value=GetValue;
					Message m=mhandler.obtainMessage(5);
					mhandler.sendMessage(m);
				}
				
				if(!GetNowValue.equals(NowValue))
				{	
					NowValue=GetNowValue;
					Message m=mhandler.obtainMessage(4);
					mhandler.sendMessage(m);
				}
			
			
				
				//更新CustomList
				nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				result=connect_to_server("project/store/Type2/GetCustomList.php",nameValuePairs);
				if(result.equals("-1"))
				{
		
					
				}
				else
				{	
					
					String key2[]={"number"};
					temp=json_deconde(result, key2);
					
					
					int[] CheckArrayAdd=new int[temp.size()];
					int[] CheckArrayDelete=new int[CustomList.size()]; 
					for(int i=0;i<temp.size();i++)
					{
						CheckArrayAdd[i]=0;
						
					}
					for(int i=0;i<CustomList.size();i++)
					{
						CheckArrayDelete[i]=0;
						
					}
					
					for(int i=0;i<CustomList.size();i++)
					{	
						for(int j=0;j<temp.size();j++)
						{
							if(temp.get(j).get("number").equals(CustomList.get(i).get("number")))
									{
										CheckArrayDelete[i]=1;
										break;
									}
						}

					}
					for(int i=0;i<CustomList.size();i++)
					{
						if(CheckArrayDelete[i]==0)
						{
							CustomList.remove(i);
							
						}
						
					}
					
					for(int i=0;i<temp.size();i++)
					{	
						for(int j=0;j<CustomList.size();j++)
						{
							if(CustomList.get(j).get("number").equals(temp.get(i).get("number")))
									{
										CheckArrayAdd[i]=1;
										break;
									}
						}

					}
					
					for(int i=0;i<temp.size();i++)
					{
						if(CheckArrayAdd[i]==0)
						{
							HashMap<String,String> t = new HashMap<String,String>();
							
							t.put("number", temp.get(i).get("number"));
							CustomList.add(t);
						}
					}
					
					
			
				
				}
				//呼叫更新waitvalue
				Message m=mhandler.obtainMessage(2);
				mhandler.sendMessage(m);
				
				handler.removeCallbacks(this);
				handler.postDelayed(this, 1000000);
				
				
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
	
	Runnable CallNumber =new Runnable() {
		
		public void run() 
		{
			
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				nameValuePairs.add(new BasicNameValuePair("CallNumber",NowChooseCustom));
				String result=connect_to_server("project/store/Type2/CallNumber.php",nameValuePairs);
				//Toast.makeText(Type2Activity.this, result, Toast.LENGTH_LONG).show();
				Message m=mhandler.obtainMessage(1,result);
				mhandler.sendMessage(m);
					
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
