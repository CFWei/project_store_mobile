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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressLint("ParserError")
public class ManageActivity extends Activity {
	String SerialNumbers;
	private Thread mthread;
	private Handler mhandler,bhandler;
	private HandlerThread handlerthread;
	private ItemAdapter itemadapter;
	private GridView gridview;
	private Button add_item;

	public static ArrayList<HashMap<String,String>> item_list=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        
       Intent thisIntent = this.getIntent();
       Bundle bundle=thisIntent.getExtras();
       SerialNumbers=bundle.getString("SerialNumbers");
       gridview = (GridView) findViewById(R.id.gridView1);
       registerForContextMenu(gridview);
       
       
       
       add_item=(Button)findViewById(R.id.add_item1);
       add_item.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(ManageActivity.this,additem.class);
			
			Bundle bundle=new Bundle();
			bundle.putString("SerialNumbers",SerialNumbers);
			intent.putExtras(bundle);
			
			startActivity(intent);
		}
       });
       
       mhandler=new Handler(){
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			switch (msg.what)
    			{
    				case 1:
    					String MsgString = (String)msg.obj;
    					Toast.makeText(ManageActivity.this, MsgString, Toast.LENGTH_LONG).show();
    					break;
    				case 2:
    					setadapter();
    					break;
    				case 3:
    					((ItemAdapter)gridview.getAdapter()).notifyDataSetChanged();
    					break;
    					
    			}
    		}
    	};
    	
    	
    	handlerthread=new HandlerThread("wait");
    	handlerthread.start();
    	bhandler=new Handler(handlerthread.getLooper());
    	
    	
    	
    	
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
    

	private void setadapter()
	{	
		itemadapter=new ItemAdapter(ManageActivity.this,item_list);
		gridview.setAdapter(itemadapter);
		gridview.setOnItemClickListener(new itemclicklistener());
		
	}
	
	
	
	class itemclicklistener implements OnItemClickListener{

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{	
			NextValue nextvalue=new NextValue();
			nextvalue.setData(arg2);
			Thread thread=new Thread(nextvalue);
			thread.start();
			
		}
		class NextValue implements Runnable
		{	
			private int position;
			public void setData(int getposition)
			{
				position=getposition;
				
			}
			
			public void run() {
				
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				String ID=item_list.get(position).get("ID");
				nameValuePairs.add(new BasicNameValuePair("Item_Id",ID));
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				try {
					String result=connect_to_server("/project/store/nextvalue.php",nameValuePairs);
					if(!result.equals("fail"))
					{	
						if(result.equals("-1"))
							item_list.get(position).put("waitcheck","1");
						else
						{
							item_list.get(position).put("Now_Value",result);
							Message m=mhandler.obtainMessage(3);
							mhandler.sendMessage(m);
						}
					}
					else
					{
						
						
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
								
								for(int i=0;i<item_list.size();i++)
								{
									item_list.get(i).put("waitcheck","0");
								}
							}
							
						Message m=mhandler.obtainMessage(2);
						mhandler.sendMessage(m);
						
						ImplementItem update_value_runnable=new ImplementItem();
						update_value_runnable.setdata(2,0);
						bhandler.postDelayed(update_value_runnable,2000);
						
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.item_menu, menu);
		
	}
    
    
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId())
		{
			case  R.id.modify:
				break;
			case R.id.delete:
				ImplementItem delete_item_runnable=new ImplementItem();
				delete_item_runnable.setdata(1,info.position);
				Thread delete_item_thread=new Thread(delete_item_runnable);
				delete_item_thread.start();
				
				break;
				
		
		}
		return super.onContextItemSelected(item);
	}
	
	private class ImplementItem implements Runnable
	{	
		private int action;
		private int position;
		public void setdata(int action,int position)
		{
			this.action=action;
			this.position=position;
			
		}
		public void run() 
		{
			if(action==1)
				delete_item();
			else if(action==2)
				update_value();
			else
				;
		}
		public void update_value()
		{	
			try {
				for(int i=0;i<item_list.size();i++)
				{	
					String ID=item_list.get(position).get("ID");
					ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("ID",ID));
					nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
					String result=connect_to_server("project/store/update_value.php",nameValuePairs);
					if(!result.equals("fail"))
					{
						item_list.get(position).put("Value",result);
					}
					else
					{
						String message="更新發生錯誤";
						Message m=mhandler.obtainMessage(1,message);
						mhandler.sendMessage(m);
						
					}
					if(item_list.get(i).get("waitcheck").equals("1"))
					{
						String value=item_list.get(i).get("Value");
						String nowvalue=item_list.get(i).get("Now_Value");
						Log.v("debug", "123");
					
						if(Integer.parseInt(value)>Integer.parseInt(nowvalue))
						{	
							nameValuePairs =new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("Item_Id",ID));
							nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
							result=connect_to_server("/project/store/nextvalue.php",nameValuePairs);
							Log.v("debug", "456");
							Log.v("debug", result);
							if(!result.equals("fail"))
							{	
								item_list.get(i).put("Now_Value", result);
								item_list.get(i).put("waitcheck","0");
							}
						}
						
						
					}
					
					
				}
				Message m=mhandler.obtainMessage(3);
				mhandler.sendMessage(m);
				
				ImplementItem update_value_runnable=new ImplementItem();
				update_value_runnable.setdata(2,0);
				bhandler.postDelayed(update_value_runnable,2000);
				
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		private void delete_item()
		{
			String ID=item_list.get(position).get("ID");
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("ID",ID));
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			try {
				String result=connect_to_server("project/store/delete_item.php",nameValuePairs);
				if(result.equals("success"))
				{	
					String message="刪除成功 頁面重整";
					Message m=mhandler.obtainMessage(1,message);
					mhandler.sendMessage(m);
					ManageActivity.this.onResume();
				}
				else
				{
					String message="刪除失敗";
					Message m=mhandler.obtainMessage(1,message);
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
