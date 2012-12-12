package com.example.takenumbersystemstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Type2MainActivity extends Activity {
	public ArrayList<HashMap<String,Object>> CustomList=null;
	public ArrayList<HashMap<String, Object>> WaitItemList=new ArrayList<HashMap<String,Object>>();
	public static final int RequestCode=1;
	public String NowValue="0";
	private Handler mhandler;
	private Handler handler;
	public CustomListAdapter CLA;
	public WaitItemListAdapter WILA;
	static String SerialNumbers; 
	Timer timer = new Timer(true);
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_type2_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork() 
        .penaltyLog()
        .build());
        
         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects() 
        .penaltyLog() 
        .penaltyDeath()
        .build());
        
        mhandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case 1:
					TextView NowValueTextView=(TextView)findViewById(R.id.NowValue);
					NowValueTextView.setText(NowValue);
					break;
				case 2:
					//CLA.notifyDataSetChanged();
					WILA.notifyDataSetChanged();
					ExpandableListView ELV=(ExpandableListView)findViewById(R.id.WaitItemExpandableListView);
					ELV.requestLayout();
					break;
				case 3:
					CLA.notifyDataSetChanged();
					break;
				}
			}
        	
        	
        };
        
        Intent intent=this.getIntent();
        Bundle bundle=intent.getExtras();
        SerialNumbers=bundle.getString("SerialNumbers");
        
        CustomList=new ArrayList<HashMap<String,Object>>();
        //設定Adapter
        setAdapter();
        
        Button SettingButton=(Button)findViewById(R.id.Setting);
        SettingButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				Bundle bundle=new Bundle();
				
				intent.setClass(Type2MainActivity.this,Type2SettingPage.class);
				
				//bundle.putString("SerialNumbers",StoreInformation.get(0).get("SerialNumbers"));
				intent.putExtras(bundle);
				startActivityForResult(intent, RequestCode);
				
			}
		});
        
        Button RefreshButton=(Button)findViewById(R.id.Refresh);
        RefreshButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomList.clear();
			    CustomList=new ArrayList<HashMap<String,Object>>();
				CLA.SetAdapter(CustomList);
				CLA.notifyDataSetChanged();
				
				WaitItemList.clear();
				WaitItemList=new ArrayList<HashMap<String,Object>>();
				WILA.Setadapter(WaitItemList);
				WILA.notifyDataSetChanged();
				
				Toast.makeText(Type2MainActivity.this, "重新整理", Toast.LENGTH_SHORT).show();
			}
		});
        
    	handler=new Handler();
    	//new GetWaitItemList().execute();
    	new Timer().schedule(new MyTimerTask(), 0, 3000);
    	new Timer().schedule(new GetWaitItemListTimerTask(), 0, 3000);   
    	
        //Thread UpdateValueThread=new Thread(UpdateValue);
        //UpdateValueThread.start();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type2_main, menu);
        return true;
    }
    
    
    
    
   @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case RequestCode:
			{
				String Result=data.getExtras().getString("Button");
				if(Result.equals("Logout")){
					
					SharedPreferences account_settings = getSharedPreferences ("ACCOUNT", 0);
					SharedPreferences.Editor PE = account_settings.edit();
					PE.putString("AutoLogin","0");
					PE.commit();
					
					Intent intent = new Intent();
					intent.setClass(Type2MainActivity.this,MainActivity.class);
					
					startActivity(intent);
					Type2MainActivity.this.finish();
					
				}
				if(Result.equals("EndBusiness")){
					Intent intent = new Intent();
					Bundle bundle=new Bundle();
					intent.setClass(Type2MainActivity.this,Type2CheckActivity.class);
					
					bundle.putString("SerialNumbers",SerialNumbers);
					intent.putExtras(bundle);
					
					
					startActivity(intent);
					Type2MainActivity.this.finish();
					
				}
				
			}
			
		}
		
	}

@Override
	protected void onPause() {
	   //handler.removeCallbacks(UpdateValue);
		super.onPause();
	}

	@Override
	protected void onResume() {
		//handler.postDelayed(UpdateValue,100);
		super.onResume();
	}




Runnable UpdateValue=new Runnable() {
	
	public void run()  {
		ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
		String result=connect_to_server("project/store/Type2/UpdateValue.php",nameValuePairs);
		
		try {	
				//轉換JSONobject
				JSONObject jobject=new JSONObject(result);
				
				
		
				//取得NowValue
				String NowValueTemp=jobject.getString("NowValue");
				//若NowValue值不一樣 則更新
				if(!NowValueTemp.equals(NowValue))
				{	
					NowValue=NowValueTemp;
					Message m=mhandler.obtainMessage(1);
					mhandler.sendMessage(m);
					
				}
				
				
				//取得CustomList
				String Check=jobject.getString("CustomList").toString();
				
				if(!Check.equals("-1")){
					JSONArray jarray=jobject.getJSONArray("CustomList");
					for	(int i=0;i<jarray.length();i++)
					{	
						
						JSONObject EachCustomData=jarray.getJSONObject(i);
						
						//取得CustomNumber
						String CustomNumber=EachCustomData.getString("number");
						boolean flag=false;
						for(HashMap<String, Object> temp:CustomList){
							String CheckNumber=temp.get("Number").toString();
							if(CheckNumber.equals(CustomNumber))
							{
								flag=true;
								break;
								
							}
							
						}
						
						if(flag==false){
							//取得CustomItemList String型態
							String CustomItemListString=EachCustomData.getString("SelectItem");
							//取得TotalCost
							String TotalCost=EachCustomData.getString("TotalCost");
							//轉換成JSONArray
							JSONArray CustomItemList=new JSONArray(CustomItemListString);
							
							//建立存放拿取Item的List
							ArrayList TakenItemList=new ArrayList<HashMap<String, Object>>();
							
							for(int j=0;j<CustomItemList.length();j++)
							{
								JSONObject EachItem= CustomItemList.getJSONObject(j);
								String TakenItemID = EachItem.getString("TakenItemID");
								String ItemName = EachItem.getString("ItemName");
								String NeedValue = EachItem.getString("NeedValue");
								String Life = EachItem.getString("Life");
								String ItemPrice = EachItem.getString("ItemPrice");
								
								HashMap ItemDataMap=new HashMap<String,Object>();
								ItemDataMap.put("TakenItemID", TakenItemID);
								ItemDataMap.put("ItemName", ItemName);
								ItemDataMap.put("NeedValue", NeedValue);
								ItemDataMap.put("ItemPrice", ItemPrice);
								ItemDataMap.put("Life", Life);
								
								
								TakenItemList.add(ItemDataMap);
							}
							
							//建立一個Hashmap並將其存入
							HashMap CustomDataContent=new HashMap<String,Object>();
							CustomDataContent.put("Number", CustomNumber);
							CustomDataContent.put("SelectItem", TakenItemList);
							CustomDataContent.put("TotalCost", TotalCost);
							
							CustomList.add(CustomDataContent);
							
							
						}
					}
					
					int[] deletesp=new int[CustomList.size()];
					int sp=0;
					
					for(int i=0;i<CustomList.size();i++){
						String Number=CustomList.get(i).get("Number").toString();
						boolean flag=false;
						for(int j=0;j<jarray.length();j++)
						{
							JSONObject EachCustomData=jarray.getJSONObject(j);
							//取得CustomNumber
							String CheckCustomNumber=EachCustomData.getString("number");
			
							if(Number.equals(CheckCustomNumber)){
			
								flag=true;
								break;
							}
						
							
						}
				
						if(flag==false){
							deletesp[sp]=i;
							sp++;
							
						}
		
					}
					for(int i=sp-1;i>=0;i--){
	
						CustomList.remove(deletesp[i]);
						
					}
				
				}
				
				//更新Adapter
				Message m=mhandler.obtainMessage(3);
				mhandler.sendMessage(m);
				

				handler.removeCallbacks(this);
				handler.postDelayed(this, 3000);
					
				
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
};
    
    public void setAdapter(){
    	CLA=new CustomListAdapter(CustomList,Type2MainActivity.this);
    	ListView CustomBlockListView=(ListView)findViewById(R.id.CustomInformationListView);
    	
    	registerForContextMenu(CustomBlockListView);
    	
    	CustomBlockListView.setAdapter(CLA);
    	
    	CustomBlockListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Toast.makeText(Type2MainActivity.this, String.valueOf(arg2), Toast.LENGTH_SHORT).show();
				String Number=CustomList.get(arg2).get("Number").toString();
				new CallNumber().execute(Number);
			}
    		
    		
		});
    	
    	WILA=new WaitItemListAdapter(WaitItemList, this);
    	ExpandableListView ELV=(ExpandableListView)findViewById(R.id.WaitItemExpandableListView);
    	
    	
    	ELV.setOnGroupClickListener(new OnGroupClickListener() {
			
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				
				return true;
			}
		});
    	ELV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,
					int arg3, long arg4) {
				// TODO Auto-generated method stub
				//Toast.makeText(Type2MainActivity.this, String.valueOf(arg2)+" "+String.valueOf(arg3), Toast.LENGTH_SHORT).show();
				new ChangeWaitItemState().execute(String.valueOf(arg2),String.valueOf(arg3));
				return true;
			}
		});
    	
    	ELV.setAdapter(WILA);
			
		}

    	
    	
    	
    	
    
    
    
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
    	 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		 final int ItemPosition = info.position;
		 switch(item.getItemId())
		 {
		 	case 0:
		 			Toast.makeText(Type2MainActivity.this, "刪除"+String.valueOf(ItemPosition), Toast.LENGTH_SHORT).show();
		 			break;
		 
		 }
    	
		return super.onContextItemSelected(item);
		
		 
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 0, 0, "刪除");
		
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
    
    class UpdateValue extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			String result=connect_to_server("project/store/Type2/UpdateValue.php",nameValuePairs);
			return result;
		}
		private void setList(String result){
			try {	
				//轉換JSONobject
				JSONObject jobject=new JSONObject(result);
				//取得NowValue
				String NowValueTemp=jobject.getString("NowValue");
				//若NowValue值不一樣 則更新
				if(!NowValueTemp.equals(NowValue))
				{	
					NowValue=NowValueTemp;
					Message m=mhandler.obtainMessage(1);
					mhandler.sendMessage(m);
					
				}
				
				//取得CustomList
				String Check=jobject.getString("CustomList").toString();
				
				if(!Check.equals("-1")){
					JSONArray jarray=jobject.getJSONArray("CustomList");
					
					for	(int i=0;i<jarray.length();i++)
					{	
						
						JSONObject EachCustomData=jarray.getJSONObject(i);
						
						//取得CustomNumber
						String CustomNumber=EachCustomData.getString("number");
						
						boolean flag=false;
						int pos=-1;
						
						for(int j=0;j<CustomList.size();j++){
							HashMap<String, Object>temp=CustomList.get(j);
							String CheckNumber=temp.get("Number").toString();
							if(CheckNumber.equals(CustomNumber))
							{
								flag=true;
								pos=j;
								break;
								
							}
							
						}
						String CustomItemListString=EachCustomData.getString("SelectItem");
						JSONArray CustomItemList=new JSONArray(CustomItemListString);
						if(flag==false){
							//取得CustomItemList String型態
							
							//取得TotalCost
							String TotalCost=EachCustomData.getString("TotalCost");
							//轉換成JSONArray
							
							
							//建立存放拿取Item的List
							ArrayList TakenItemList=new ArrayList<HashMap<String, Object>>();
							
							for(int j=0;j<CustomItemList.length();j++)
							{
								JSONObject EachItem= CustomItemList.getJSONObject(j);
								String TakenItemID = EachItem.getString("TakenItemID");
								String ItemName = EachItem.getString("ItemName");
								String NeedValue = EachItem.getString("NeedValue");
								String Life = EachItem.getString("Life");
								String ItemPrice = EachItem.getString("ItemPrice");
								
								HashMap ItemDataMap=new HashMap<String,Object>();
								ItemDataMap.put("TakenItemID", TakenItemID);
								ItemDataMap.put("ItemName", ItemName);
								ItemDataMap.put("NeedValue", NeedValue);
								ItemDataMap.put("ItemPrice", ItemPrice);
								ItemDataMap.put("Life", Life);
								
								
								TakenItemList.add(ItemDataMap);
							}
							
							//建立一個Hashmap並將其存入
							HashMap CustomDataContent=new HashMap<String,Object>();
							CustomDataContent.put("Number", CustomNumber);
							CustomDataContent.put("SelectItem", TakenItemList);
							CustomDataContent.put("TotalCost", TotalCost);
							
							CustomList.add(CustomDataContent);
							
							
						}
						
						else
						{	
							if(pos!=-1){
								ArrayList<HashMap<String, Object>> temp=(ArrayList<HashMap<String, Object>>) CustomList.get(pos).get("SelectItem");
								for(int j=0;j<temp.size();j++){
									HashMap ItemDataMap=temp.get(j);
									//String Life=temp.get(j).get("Life").toString();
									String TakenItemID=ItemDataMap.get("TakenItemID").toString();
									
									for(int k=0;k<CustomItemList.length();k++){
										JSONObject EachItem= CustomItemList.getJSONObject(j);
										String MatchTakenItemID = EachItem.getString("TakenItemID");
										if(MatchTakenItemID.equals(TakenItemID)){
											String Life = EachItem.getString("Life");
											ItemDataMap.put("Life", Life);
											
										}
										
										
									}
									
									
									
								}
								
								
							}
							
						}
						
					}
					
					int[] deletesp=new int[CustomList.size()];
					int sp=0;
					
					for(int i=0;i<CustomList.size();i++){
						String Number=CustomList.get(i).get("Number").toString();
						boolean flag=false;
						for(int j=0;j<jarray.length();j++)
						{
							JSONObject EachCustomData=jarray.getJSONObject(j);
							//取得CustomNumber
							String CheckCustomNumber=EachCustomData.getString("number");
			
							if(Number.equals(CheckCustomNumber)){
			
								flag=true;
								break;
							}
						
							
						}
				
						if(flag==false){
							deletesp[sp]=i;
							sp++;
						}
		
					}
					for(int i=sp-1;i>=0;i--){
						CustomList.remove(deletesp[i]);
						
					}
				
				}

		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			setList(result);
			Message m=mhandler.obtainMessage(3);
			mhandler.sendMessage(m);
			
			super.onPostExecute(result);
			CLA.notifyDataSetChanged();
			
		}
		
		
    	
    	
    }
    class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			handler.post(new Runnable() {
				
				public void run() {
					new UpdateValue().execute();
					Log.v("debug", "UpdateValue");
					
				}
			});
			
		}
    	
    	
    }
    
    class GetWaitItemListTimerTask extends TimerTask{

		@Override
		public void run() {
			handler.post(new Runnable() {
				
				public void run() {
					
					new GetWaitItemList().execute();
					Log.v("debug", "GetWaitItemList");
				}
			});
			
		}
    	
    	
    }
    
    class GetWaitItemList extends AsyncTask<String, Void, String>{
    	
		@Override
		protected String doInBackground(String... params) {
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			String result=connect_to_server("project/store/Type2/GetItemQueueList.php",nameValuePairs);
			

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			/*
			for(int i=0;i<WaitItemList.size();i++){
				String ItemName=WaitItemList.get(i).get("ItemName").toString();
				Toast.makeText(Type2MainActivity.this, ItemName, Toast.LENGTH_SHORT).show();
			}*/
			
			processData(result);
			ExpandableListView ELV=(ExpandableListView)findViewById(R.id.WaitItemExpandableListView);
			int count=WaitItemList.size();
			
			for(int i=0;i<count;i++){
				ELV.expandGroup(i);
				
			}
			Log.v("debug", WaitItemList.toString());
			
			Message m=mhandler.obtainMessage(2);
			mhandler.sendMessage(m);
			WILA.notifyDataSetChanged();
			ELV.requestLayout();
			super.onPostExecute(result);
			
			
		}
		public void processData(String result){
			if(!result.equals("null")){
				try {
					
					JSONArray ItemList=new JSONArray(result);
					for(int i=0;i<ItemList.length();i++){
						JSONObject ItemData=ItemList.getJSONObject(i);
						String ItemName=ItemData.getString("ItemName");
						String ItemID=ItemData.getString("ItemID");
						
						int flag=-1;
						//找尋是否已有此Item
						for(int k=0;k<WaitItemList.size();k++){
							String CheckItemID=WaitItemList.get(k).get("ItemID").toString();
							if(CheckItemID.equals(ItemID)){
								flag=k;
								
							}
							
						}
						
						HashMap AddItem=null;
						ArrayList<HashMap<String, Object>> WaitCustomList=null;
						//如果有此Item存在 則將AddItem指向已存在的位置 並且將WaitCustomList指向對的位置
						//若不存在則new一個新的空間
						if(flag==-1){
							AddItem=new HashMap<String, Object>();
							AddItem.put("ItemName", ItemName);
							AddItem.put("ItemID", ItemID);
							WaitCustomList=new ArrayList<HashMap<String,Object>>();
						}
						else{
							AddItem=WaitItemList.get(flag);
							WaitCustomList=(ArrayList<HashMap<String, Object>>) WaitItemList.get(flag).get("WaitCustomList");
							
						}
						
						
						JSONArray WaitCustomListArray=ItemData.getJSONArray("WaitingCustomData");
						
						for(int j=0;j<WaitCustomListArray.length();j++){
							JSONObject WaitCustomData=WaitCustomListArray.getJSONObject(j);
							String CustomNumber=WaitCustomData.getString("CustomNumber");
							String Quantity=WaitCustomData.getString("Quantity");
							String Life=WaitCustomData.getString("Life");
							
							
							//檢查號碼是否已存在 若存在則不更新
							boolean checkflag=false;
							int addflag=-1;
							for(int l=0;l<WaitCustomList.size();l++){
								String checkTemp=WaitCustomList.get(l).get("CustomNumber").toString();
								if(checkTemp.equals(CustomNumber)){
									checkflag=true;
									addflag=l;
									break;
								}
								
							}
							
			
							if(checkflag==false){
							HashMap<String, Object> WaitCustomDataMap =new HashMap<String, Object>();
							WaitCustomDataMap.put("CustomNumber", CustomNumber);
							WaitCustomDataMap.put("Quantity", Quantity);
							WaitCustomDataMap.put("Life", Life);
							WaitCustomList.add(WaitCustomDataMap);	
							}
							else{
								HashMap<String, Object> WaitCustomDataMa =WaitCustomList.get(addflag);
								WaitCustomDataMa.put("Life", Life);
								
							}
							
						}
						
						if(flag==-1){
							AddItem.put("WaitCustomList", WaitCustomList);
							WaitItemList.add(AddItem);
						}
						
						
						int[] deletesp=new int[WaitCustomList.size()];
						int sp=0;
						for(int m=0;m<WaitCustomList.size();m++){
							String Checktemp=WaitCustomList.get(m).get("CustomNumber").toString();
							boolean CheckFlag=false;
							
							for(int n=0;n<WaitCustomListArray.length();n++){
								JSONObject WaitCustomData=WaitCustomListArray.getJSONObject(n);
								String CustomNumber=WaitCustomData.getString("CustomNumber");
								if(CustomNumber.equals(Checktemp)){
									CheckFlag=true;
									break;
								}
								
								
							}
							
							if(CheckFlag==false){
								deletesp[sp]=m;
								sp++;
							}
							
							
	
						}
						
						
						
						for(int o=sp-1;o>=0;o--){
							WaitCustomList.remove(deletesp[o]);
						}
						
					}
					
					
					int[] deletesp=new int[WaitItemList.size()];
					int sp=0;
	
					for(int i=0;i<WaitItemList.size();i++){
						String CheckItemID=WaitItemList.get(i).get("ItemID").toString();
						boolean CheckFlag=false;
						for(int j=0;j<ItemList.length();j++){
							JSONObject ItemData=ItemList.getJSONObject(j);
							String ItemID=ItemData.getString("ItemID");
							if(CheckItemID.equals(ItemID)){
								CheckFlag=true;
								break;
								}
							
						}
						
						if(CheckFlag==false){
							deletesp[sp]=i;
							sp++;
						}
						
					}
					
	
					for(int o=sp-1;o>=0;o--){
						WaitItemList.remove(deletesp[o]);
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				for(int i=0;i<WaitItemList.size();i++){
					WaitItemList.remove(i);
					
				}
				
			}
			
		}
		
    	
    }
    
    
    class DeleteNumber extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			nameValuePairs.add(new BasicNameValuePair("CallNumber",params[0]));
			String result=connect_to_server("project/store/Type2/DeleteNumber.php",nameValuePairs);
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
    	
    }
    
    
    
    
    
    
    class CallNumber extends AsyncTask<String, Void, String>{
    	
    	int record=-1;
		@Override
		protected String doInBackground(String... params) {
			ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			nameValuePairs.add(new BasicNameValuePair("CallNumber",params[0]));
			String result=connect_to_server("project/store/Type2/CallNumber.php",nameValuePairs);
			
			//waititemlist尚未清空
			if(result.equals(String.valueOf("-1"))){
				return "-1";
			}
			
			//找出LastNumber在CustomList的index並紀錄方便之後移除
			JSONObject jobject = null;
			String LastNumber = null;
			try {
				jobject = new JSONObject(result);
				LastNumber=jobject.getString("LastNumber");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<CustomList.size();i++){
				String temp=CustomList.get(i).get("Number").toString();
				if(LastNumber.equals(temp)){
					record=i;
				}
				
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			//若result為-1 代表waititemlist未清空
			if(result.equals("-1")){
				Toast.makeText(Type2MainActivity.this, "此客戶所需商品尚未製作完成", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//解JSONObject
			JSONObject jobject = null;
			String NowValueTemp = null;
			try {
				jobject = new JSONObject(result);
				NowValueTemp=jobject.getString("NowValue");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//若NowValueTemp!=NowValue則更新
			if(!NowValueTemp.equals(NowValue))
			{	
				NowValue=NowValueTemp;
				TextView NowValueTextView=(TextView)findViewById(R.id.NowValue);
				NowValueTextView.setText(NowValue);
				
			}
			
			//移除CustomList上的此客戶
			if(record!=-1){
				CustomList.remove(record);
				record=-1;
			}
			CLA.notifyDataSetChanged();
			
			
			
		}
		
		
    	
    } 
	class ChangeWaitItemState extends AsyncTask<String, Void, String[]>{
		
		@Override
		protected String[] doInBackground(String... params) {
			
					
			
			String[] Result=new String[3];
			try {
				String group=params[0];
				String child=params[1];
				String ItemID=WaitItemList.get(Integer.parseInt(group)).get("ItemID").toString();
				
				ArrayList<HashMap<String, Object>> WaitCustomList= (ArrayList<HashMap<String, Object>>) WaitItemList.get(Integer.parseInt(group)).get("WaitCustomList");
				HashMap<String, Object> SelectCustom=WaitCustomList.get(Integer.parseInt(child));
				String SelectNumber = SelectCustom.get("CustomNumber").toString();
				
				
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				nameValuePairs.add(new BasicNameValuePair("ItemID",ItemID));
				nameValuePairs.add(new BasicNameValuePair("CustomNumber",SelectNumber));
				
				
				String result=connect_to_server("project/store/Type2/ItemFinish.php",nameValuePairs);
				
				Result[0]=group;
				Result[1]=child;
				Result[2]=result;
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			//ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
			//nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
			//nameValuePairs.add(new BasicNameValuePair("CallNumber",params[0]));
			//
			return Result;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			// TODO Auto-generated method stub
			
			int Group=Integer.parseInt(result[0]);
			int child=Integer.parseInt(result[1]);
			
			if(result[2].equals("1"))
			{
				ArrayList<HashMap<String, Object>> WaitCustomList=(ArrayList<HashMap<String, Object>>)WaitItemList.get(Group).get("WaitCustomList");
				WaitCustomList.remove(child);
				
				
				
			}
			if(result[2].equals("2"))
			{
				ArrayList<HashMap<String, Object>> WaitCustomList=(ArrayList<HashMap<String, Object>>)WaitItemList.get(Group).get("WaitCustomList");
				HashMap<String, Object> ItemMap=WaitCustomList.get(child);
				ItemMap.put("Life", "2");
			}
			
			Message m=mhandler.obtainMessage(2);
			mhandler.sendMessage(m);
			WILA.notifyDataSetChanged();
			ExpandableListView ELV=(ExpandableListView)findViewById(R.id.WaitItemExpandableListView);
			ELV.requestLayout();
	
		
			
			
			super.onPostExecute(result);
		}
		
	}	
    
    class WaitItemListAdapter extends BaseExpandableListAdapter{
    	ArrayList<HashMap<String, Object>> WaitItemList;
    	Context context;
    	
    	public WaitItemListAdapter(ArrayList<HashMap<String, Object>> WaitItemList,Context context){
    		this.WaitItemList=WaitItemList;
    		this.context=context;
    	}
    	
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			ArrayList<HashMap<String, Object>> CustomList=(ArrayList<HashMap<String, Object>>) WaitItemList.get(arg0).get("WaitCustomList");
			String CustomNumber=CustomList.get(arg1).get("CustomNumber").toString();
			return CustomNumber;
		}

		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return arg1;
		}

		public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
				ViewGroup arg4) {
			ArrayList<HashMap<String, Object>> CustomList=(ArrayList<HashMap<String, Object>>) WaitItemList.get(arg0).get("WaitCustomList");
			String CustomNumber=CustomList.get(arg1).get("CustomNumber").toString();
			String QuantityNumber=CustomList.get(arg1).get("Quantity").toString();
			String Life=CustomList.get(arg1).get("Life").toString();
			
			LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view=layoutinflater.inflate(R.layout.waititemcustomlayout, null);
			
			TextView CustomNumberTextView=(TextView)view.findViewById(R.id.CustomNumberText);
			CustomNumberTextView.setText(CustomNumber);
			
			TextView QuantityNumberTextView=(TextView)view.findViewById(R.id.QuantityText);
			QuantityNumberTextView.setText(QuantityNumber);
			
			if(Life.equals("2")){
				TextView Status=(TextView)view.findViewById(R.id.Status);
				Status.setText("製作中");
			}
			
			
			return view;
		}

		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			ArrayList<HashMap<String, Object>> CustomList=(ArrayList<HashMap<String, Object>>) WaitItemList.get(arg0).get("WaitCustomList");
			return CustomList.size();
		}

		public Object getGroup(int arg0) {
			// TODO Auto-generated method stub
			return WaitItemList.get(arg0).get("ItemName").toString();
		}

		public int getGroupCount() {
			// TODO Auto-generated method stub
			return WaitItemList.size();
		}

		public long getGroupId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public View getGroupView(int arg0, boolean arg1, View arg2,
				ViewGroup arg3) {
			// TODO Auto-generated method stub
			LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view=layoutinflater.inflate(R.layout.waititemlist, null);
			
			TextView WaitItemNameTextView=(TextView)view.findViewById(R.id.WaitItemName);
			WaitItemNameTextView.setText(WaitItemList.get(arg0).get("ItemName").toString());
			
			
			
			return view;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}
    	public void Setadapter(ArrayList<HashMap<String, Object>> WaitItemList){
    		this.WaitItemList=WaitItemList;
    		
    	}
    	
    }	
    
    
    class CustomListAdapter extends BaseAdapter{
    	public ArrayList<HashMap<String,Object>> CustomList;
    	private Context context;
    	
    	public CustomListAdapter(ArrayList<HashMap<String,Object>> CustomList,Context context){
    		this.CustomList=CustomList;
    		this.context=context;
    	}
    	
		public int getCount() {
			// TODO Auto-generated method stub
			return CustomList.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		public void SetAdapter(ArrayList<HashMap<String,Object>> CustomList){
			this.CustomList=CustomList;
			
			
		}
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view=layoutinflater.inflate(R.layout.custominformation, null);
			
			//設定Number
			TextView Number=(TextView)view.findViewById(R.id.CustomDataNumber);
			String NumberText=CustomList.get(arg0).get("Number").toString();
			Number.setText(NumberText);
			
			//設定TotalCost
			TextView TotalCostTextView=(TextView)view.findViewById(R.id.TotalCostText);
			String TotalCostText=CustomList.get(arg0).get("TotalCost").toString();
			TotalCostTextView.setText(TotalCostText);
			
			LinearLayout FullLayout=(LinearLayout)view.findViewById(R.id.CustomLinearLayout);
			if(NumberText.equals(NowValue)){
				FullLayout.setBackgroundColor(Color.GREEN);
			}
			else{
				FullLayout.setBackgroundColor(Color.WHITE);
				
			}
			
			//設定商品資訊
			LinearLayout ll=(LinearLayout)view.findViewById(R.id.ItemListLayout);
			ArrayList<HashMap<String,Object>> ItemList=(ArrayList) CustomList.get(arg0).get("SelectItem");
			for(HashMap<String,Object> temp:ItemList){
				
				View ItemView=layoutinflater.inflate(R.layout.customiteminformation, null);
				
				//設定ItemName
				String ItemName=temp.get("ItemName").toString();
				TextView ItemNameTextView=(TextView)ItemView.findViewById(R.id.ItemName);
				ItemNameTextView.setText(ItemName);
				
				String NeedValue=temp.get("NeedValue").toString();
				TextView NeedValueTextView=(TextView)ItemView.findViewById(R.id.ItemQuantity);
				NeedValueTextView.setText(NeedValue);
				
				String ItemPrice=temp.get("ItemPrice").toString();
				TextView ItemPriceTextView=(TextView)ItemView.findViewById(R.id.ItemPrice);
				ItemPriceTextView.setText(ItemPrice);
				
				String Life=temp.get("Life").toString();
				String Status=null;
				if(Life.equals("0"))
					Status="未完成";
				else if(Life.equals("1"))
					Status="已完成";
				else if(Life.equals("2"))
					Status="製作中";
				else
					Status=Life;
				TextView LifeTextView=(TextView)ItemView.findViewById(R.id.Status);
				LifeTextView.setText(Status);
				
				
				ll.addView(ItemView);
			}
			
		
			
			

			
			/*
			ArrayList<HashMap<String,Object>> ItemList=(ArrayList) CustomList.get(arg0).get("SelectItem");
			
			
			CustomItemListAdapter CILA=new CustomItemListAdapter(ItemList, context);
			
			ListView listview=(ListView)view.findViewById(R.id.CustomItemListView);
			listview.setAdapter(CILA);
			*/
		
			
			return view;
		}
		
		
    	
    }

    
}
