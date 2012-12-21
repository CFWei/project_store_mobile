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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressLint("ParserError")
public class ManageActivity extends Activity {
	static String SerialNumbers;
	SimpleAdapter LookUpCustomSimpleAdapter ;
	ArrayList<HashMap<String,String>> ShowLookUpCustom=new ArrayList<HashMap<String,String>> ();
	ArrayList<HashMap<String,String>> custom=new ArrayList<HashMap<String,String>>();
	private Thread mthread;
	private static Handler mhandler;
	private static Handler bhandler;
	private HandlerThread handlerthread;
	private ItemAdapter itemadapter;
	private GridView gridview;
	ImplementItem update_value_runnable=new ImplementItem();
	private static final int Logout=1;

	public static ArrayList<HashMap<String,String>> item_list=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        
       Intent thisIntent = this.getIntent();
       Bundle bundle=thisIntent.getExtras();
       SerialNumbers=bundle.getString("SerialNumbers");
       gridview = (GridView) findViewById(R.id.StoreItemGridView);
       //registerForContextMenu(gridview);
       

     
       
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
    				case 4:
    					SharedPreferences account_settings = getSharedPreferences ("ACCOUNT", 0);
    					SharedPreferences.Editor PE = account_settings.edit();
    					PE.putString("AutoLogin","0");
    					PE.commit();
    					
    					Intent intent = new Intent();
    					intent.setClass(ManageActivity.this,MainActivity.class);
    					
    					startActivity(intent);
    					ManageActivity.this.finish();
    					break;
    				case 5:
    					String choose=Integer.toString(msg.arg1);
    					
    					if(choose.equals("-2"))
    						custom=(ArrayList<HashMap<String,String>>)msg.obj;
    					ShowLookUpCustom.clear();
    					
    					for(int i=0;i<custom.size();i++)
    					{	
    						String checkvalue=custom.get(i).get("life");

    						if(choose.equals("-1")||checkvalue.equals(choose))
    						{
        						HashMap<String,String> temp = new HashMap<String,String>();
    							temp.put("number",custom.get(i).get("number"));
    							temp.put("PhoneNumber",custom.get(i).get("PhoneNumber"));
    							temp.put("life",custom.get(i).get("life"));
    							ShowLookUpCustom.add(temp);
    						}
    						
    					}
    					if(!choose.equals("-2"))
    					{
    						LookUpCustomSimpleAdapter.notifyDataSetChanged();
    						break;
    					}
    					LayoutInflater layoutinflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    					View LookUpCustomView=layoutinflater.inflate(R.layout.lookupcustom, null);
    					ListView list=(ListView)LookUpCustomView.findViewById(R.id.LookUpCustomListVIew);
    						
    					String key[]={"number","PhoneNumber","life"};
    					
    					LookUpCustomSimpleAdapter =new SimpleAdapter( 
   							 ManageActivity.this, 
   							 ShowLookUpCustom,
   							 R.layout.lookupcustomview,
   							 key,
   							 new int[] { R.id.CustomNumberTextView,R.id.CustomIDTextView,R.id.CustomLifeTextView} );
    					
    					list.setAdapter(LookUpCustomSimpleAdapter);
    					
    					String [] m={"全部","未服務","已服務","已刪除"};  
    					Spinner spinner = (Spinner)LookUpCustomView.findViewById(R.id.LookUpCustomSpinner);
    					ArrayAdapter<String> adapter=new ArrayAdapter<String>(ManageActivity.this,android.R.layout.simple_spinner_item,m);
    					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    					
    					spinner.setOnItemSelectedListener(new LookUpCustomOnItemSelectedListener());
    					spinner.setAdapter(adapter);  
    					spinner.setSelection(1);
    					
    				
    					Builder MyAlertDialog = new AlertDialog.Builder(ManageActivity.this);
    					
    					MyAlertDialog.setTitle("客戶清單");
    					MyAlertDialog.setView(LookUpCustomView);
    					MyAlertDialog.setPositiveButton("關閉",null);
    			
    					MyAlertDialog.show();
    					break;
    				case 6:
    					String position=(String)msg.obj;
    					String ID=item_list.get(Integer.parseInt(position)).get("ID");
    					
    					Intent thisIntent = new Intent();
    					thisIntent.setClass(ManageActivity.this,itemfullscreen.class);
    					
    					Bundle bundle=new Bundle();
    					bundle.putString("SerialNumbers",SerialNumbers);
    					bundle.putString("ID",ID);
    					
    					thisIntent.putExtras(bundle);
    					
    					startActivity(thisIntent);
    					
    					break;
    						
    					
    			}
    		}
    	};
    	
    	
    	
    	setButton();
       
         
    	handlerthread=new HandlerThread("wait");
    	handlerthread.start();
    	bhandler=new Handler(handlerthread.getLooper());
    	
    	

    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(handlerthread.isAlive())
			handlerthread.quit();
		handlerthread=new HandlerThread("wait");
    	handlerthread.start();
    	bhandler=new Handler(handlerthread.getLooper());
		
		mthread=new Thread(get_item_list);
		mthread.start();
	}
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		handlerthread.quit();
		if(bhandler!=null)
			bhandler.removeCallbacks(update_value_runnable);
	}
    
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_manage, menu);
        return true;
    }
    
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
			case Logout:
					if(resultCode==333){
						SharedPreferences account_settings = getSharedPreferences ("ACCOUNT", 0);
						SharedPreferences.Editor PE = account_settings.edit();
						PE.putString("AutoLogin","0");
						PE.commit();
						
						Intent intent = new Intent();
						intent.setClass(ManageActivity.this,MainActivity.class);
						startActivity(intent);
						ManageActivity.this.finish();
						
					}
					break;
		
		}
	}

	public void setButton() {
		Button refresh = (Button) findViewById(R.id.RefreshButton);
		
		refresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ManageActivity.this.onResume();
				Toast.makeText(ManageActivity.this, "重新刷新頁面",
						Toast.LENGTH_SHORT).show();

			}
		});
		Button selectMenuButton=(Button) findViewById(R.id.SelectMenuButton);
		selectMenuButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ManageActivity.this, Type1Setting.class);

				Bundle bundle = new Bundle();
				bundle.putString("SerialNumbers", SerialNumbers);
				intent.putExtras(bundle);

				startActivityForResult(intent, Logout);
			}
		});
		
	

	}

	private void setadapter()
	{	
		itemadapter=new ItemAdapter(ManageActivity.this,item_list);
		gridview.setAdapter(itemadapter);
		//gridview.setOnItemClickListener(new itemclicklistener());
		
	}
	
	
	/*
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
						{	
							if(item_list.get(position).get("waitcheck").equals("0"))
							{
								item_list.get(position).put("waitcheck","1");
								item_list.get(position).put("hintcontent","WAIT NEXT VALUE");
								item_list.get(position).put("hintvalue","0");
							}
							else
							{
								item_list.get(position).put("waitcheck","0");
								item_list.get(position).put("hintcontent","");
								item_list.get(position).put("hintvalue","0");
								
							}
							
							Message m=mhandler.obtainMessage(3);
							mhandler.sendMessage(m);
						}
						else
						{
							item_list.get(position).put("Now_Value",result);
							Message m=mhandler.obtainMessage(3);
							mhandler.sendMessage(m);
							
							item_list.get(position).put("hintcontent","NEW VALUE");
							item_list.get(position).put("hintvalue","1");
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
			
			
		}
		
	}
	*/
	
	static class  NextValue implements Runnable
	{	
		private int position;
		private String choose;
		public void setData(int getposition,String mchoose)
		{
			position=getposition;
			choose=mchoose;
		}
		
		public void run() {
			
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			String ID=item_list.get(position).get("ID");
			nameValuePairs.add(new BasicNameValuePair("Item_Id",ID));
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			nameValuePairs.add(new BasicNameValuePair("choose",choose));
			try {
				String result=connect_to_server("/project/store/nextvalue.php",nameValuePairs);
				if(!result.equals("-2"))
				{	
					if(result.equals("-1"))
					{	/*
						if(item_list.get(position).get("waitcheck").equals("0"))
						{
							item_list.get(position).put("waitcheck","1");
							item_list.get(position).put("hintcontent","WAIT NEXT VALUE");
							item_list.get(position).put("hintvalue","0");
						}
						else
						{
							item_list.get(position).put("waitcheck","0");
							item_list.get(position).put("hintcontent","");
							item_list.get(position).put("hintvalue","0");
							
						}
						
						Message m=mhandler.obtainMessage(3);
						mhandler.sendMessage(m);
						*/
					}
					else
					{
						item_list.get(position).put("Now_Value",result);
						Message m=mhandler.obtainMessage(3);
						mhandler.sendMessage(m);
						
						item_list.get(position).put("hintcontent","NEW VALUE");
						item_list.get(position).put("hintvalue","1");
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
		
		
	}
	
	
	
	private Runnable get_item_list=new  Runnable() {
		public void run() 
		{	
	
			try {	
					Log.v("debug", "get_item_list");
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
								String key[]={"ID","Name","State","Value","Now_Value"};
								if(!result.equals("null"))
								{	
									item_list=json_deconde(result,key);
								
									for(int i=0;i<item_list.size();i++)
									{
										item_list.get(i).put("waitcheck","0");
										item_list.get(i).put("hintcontent","");
										item_list.get(i).put("hintvalue","0");
										item_list.get(i).put("waitcustomvalue","0");
									}
									
									
									Message m=mhandler.obtainMessage(2);
									mhandler.sendMessage(m);

									update_value_runnable.setdata(2,0);
									bhandler.postDelayed(update_value_runnable,2000);
								
								}
						
							
					
						
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
	
/*	
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
		Intent intent = new Intent();
		String ID=item_list.get(info.position).get("ID");
		Bundle bundle=new Bundle();
		switch(item.getItemId())
		{	
			case R.id.itemfullscreen:
				
				
				intent.setClass(ManageActivity.this,itemfullscreen.class);
				bundle.putString("SerialNumbers",SerialNumbers);
				bundle.putString("ID",ID);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case  R.id.modify:
				intent.setClass(ManageActivity.this,edititem.class);
				bundle.putString("SerialNumbers",SerialNumbers);
				bundle.putString("ID",ID);
				intent.putExtras(bundle);
				startActivity(intent);
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
*/	
	public static class ImplementItem implements Runnable
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
			else if(action==3)
				FullScreen();
			else
				;
		}
		
		public void FullScreen(){
			//Toast.makeText(ManageActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
			Message m=mhandler.obtainMessage(6,String.valueOf(position));
			mhandler.sendMessage(m);
		}
		
		public void update_value()
		{	
			try {
				String key[]={"WaitNumValue","State","Value","Now_Value"};
				for(int i=0;i<item_list.size();i++)
				{	
						
					String ID=item_list.get(i).get("ID");
					ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("ID",ID));
					nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
					String result=connect_to_server("project/store/update_value.php",nameValuePairs);
		
					if(!result.equals("-1"))
					{	
						ArrayList<HashMap<String,String>> list=json_deconde(result,key);
						item_list.get(i).put("Value",list.get(0).get("Value"));
						item_list.get(i).put("Now_Value",list.get(0).get("Now_Value"));
						item_list.get(i).put("waitcustomvalue",list.get(0).get("WaitNumValue"));
					}
					else
					{
						String message="更新發生錯誤";
						Message m=mhandler.obtainMessage(1,message);
						mhandler.sendMessage(m);
					}
					
					/*
					nameValuePairs =new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("ID",ID));
					nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
					nameValuePairs.add(new BasicNameValuePair("Now_Value",item_list.get(i).get("Now_Value")));
					result=connect_to_server("project/store/GetWaitCustomValue.php",nameValuePairs);
					item_list.get(i).put("waitcustomvalue",result);
					*/
					
					/*
					if(item_list.get(i).get("waitcheck").equals("1"))
					{
						String value=item_list.get(i).get("Value");
						String nowvalue=item_list.get(i).get("Now_Value");
					
						if(Integer.parseInt(value)>Integer.parseInt(nowvalue))
						{	
							nameValuePairs =new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("Item_Id",ID));
							nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
							result=connect_to_server("/project/store/nextvalue.php",nameValuePairs);
							if(!result.equals("-1"))
							{	
								item_list.get(i).put("Now_Value", result);
								item_list.get(i).put("waitcheck","0");
								item_list.get(i).put("hintcontent","NEW VALUE");
								item_list.get(i).put("hintvalue","1");
							}
						}
						
						
					}
					*/
					if(!item_list.get(i).get("hintvalue").equals("0"))
					{
						if(item_list.get(i).get("hintvalue").equals("3"))
							{
								item_list.get(i).put("hintcontent","");
								item_list.get(i).put("hintvalue","0");
								
							}
						else
							{
								int count=Integer.parseInt(item_list.get(i).get("hintvalue"));
								count++;
								item_list.get(i).put("hintvalue",String.valueOf(count));
							
							}
					}
				}
				//notifydatasetchange()
				Message m=mhandler.obtainMessage(3);
				mhandler.sendMessage(m);
				//設定下次更新
				ImplementItem update_value_runnable=new ImplementItem();
				update_value_runnable.setdata(2,0);
				
				bhandler.postDelayed(update_value_runnable,2000);
				
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
					item_list.remove(position);
					Message m=mhandler.obtainMessage(3);
					mhandler.sendMessage(m);
					
					String message="刪除成功 頁面重整";
					m=mhandler.obtainMessage(1,message);
					mhandler.sendMessage(m);
					//ManageActivity.this.onResume();
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
	
	
	public static String connect_to_server(String program,ArrayList<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException
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
	
	
	
	public static class EditNowValue implements Runnable
	{	
		String ID;
		String EditValue;
		int position;
		
		public void setData(String mitemid,String meditvalue,int mposition)
		{
			ID=mitemid;
			EditValue=meditvalue;
			position=mposition;
		}
		
		public void run() 
		{
			
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("ID",ID));
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				nameValuePairs.add(new BasicNameValuePair("EditValue",EditValue));
				String result=connect_to_server("/project/store/editnumber.php",nameValuePairs);
				if(result.equals(EditValue))
				{	
					item_list.get(position).put("Now_Value", EditValue);
					item_list.get(position).put("waitcheck","0");
					item_list.get(position).put("hintcontent","NEW VALUE");
					item_list.get(position).put("hintvalue","1");
					
					Message m=mhandler.obtainMessage(1,"跳號成功");
					mhandler.sendMessage(m);	
				}
				else
				{
					Message m=mhandler.obtainMessage(1,"跳號失敗");
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
	
	
	public static class GetCustomInformation implements Runnable
	{
		String ID;
		public void setData(String mitemid)
		{
			ID=mitemid;
		
		}
		
		public void run() 
		{
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("ID",ID));
				nameValuePairs.add(new BasicNameValuePair("store",SerialNumbers));
				String result=connect_to_server("/project/store/LookUpCustomInformatio.php",nameValuePairs);
				
				if(!result.equals("null"))
				{	
					
					String key[]={"number","custom_id","life","PhoneNumber"};
					ArrayList<HashMap<String,String>> custom=json_deconde(result,key);
					
					
					Message msg = mhandler.obtainMessage();
					msg.what=5;
					msg.obj=custom;
					msg.arg1=-2;
					mhandler.sendMessage(msg);
				}
				else
				{
					Message msg = mhandler.obtainMessage();
					msg.what=1;
					msg.obj=result;
					mhandler.sendMessage(msg);
				}
				
				
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
	}
	
	
	class LookUpCustomOnItemSelectedListener implements OnItemSelectedListener
	{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Message msg = mhandler.obtainMessage();
			msg.what=5;
			msg.obj=custom;
			msg.arg1=arg2-1;
			mhandler.sendMessage(msg);
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	public static ArrayList<HashMap<String,String>> json_deconde(String jsonString,String[] key) throws JSONException
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
