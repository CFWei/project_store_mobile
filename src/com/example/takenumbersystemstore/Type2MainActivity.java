package com.example.takenumbersystemstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class Type2MainActivity extends Activity {
	public ArrayList<HashMap<String,Object>> CustomList=null;
	public String NowValue="0";
	private Handler mhandler;
	public CustomListAdapter CLA;
	String SerialNumbers; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type2_main);
        
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
				
				}
			}
        	
        	
        };
        
        Intent intent=this.getIntent();
        Bundle bundle=intent.getExtras();
        SerialNumbers=bundle.getString("SerialNumbers");
        
        CustomList=new ArrayList<HashMap<String,Object>>();
        //設定Adapter
        setAdapter();
        
        
        Thread UpdateValueThread=new Thread(UpdateValue);
        UpdateValueThread.start();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type2_main, menu);
        return true;
    }

   Runnable UpdateValue=new Runnable() {
	
	public void run()  {
		ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
		String result=connect_to_server("project/store/Type2/UpdateValue.php",nameValuePairs);
		
		try {	
				//轉換JSONobject
				JSONObject jobject=new JSONObject(result);
				//取得CustomList
				JSONArray jarray=jobject.getJSONArray("CustomList");
				
				String NowValueTemp=jobject.getString("NowValue");
				
				if(!NowValueTemp.equals(NowValue))
				{	
					NowValue=NowValueTemp;
					Message m=mhandler.obtainMessage(1);
					mhandler.sendMessage(m);
					
				}
				
				
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
				for(int p=0;p<CustomList.size();p++)
				{
					String number=CustomList.get(p).get("Number").toString();
					
					ArrayList<HashMap<String, Object>> testItem=(ArrayList<HashMap<String, Object>>) CustomList.get(p).get("SelectItem");
					for(int r=0;r<testItem.size();r++)
					{
						String ItemName=testItem.get(r).get("ItemName").toString();
					
					}
					
					
				}
					
					
				
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
};
    
    public void setAdapter(){
    	CLA=new CustomListAdapter(CustomList,Type2MainActivity.this);
    	ListView CustomBlockListView=(ListView)findViewById(R.id.CustomInformationListView);
    	CustomBlockListView.setAdapter(CLA);
    	
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

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view=layoutinflater.inflate(R.layout.custominformation, null);
			
			TextView Number=(TextView)view.findViewById(R.id.CustomDataNumber);
			String NumberText=CustomList.get(arg0).get("Number").toString();
			Number.setText(NumberText);
			
			TextView TotalCostTextView=(TextView)view.findViewById(R.id.TotalCostText);
			String TotalCostText=CustomList.get(arg0).get("TotalCost").toString();
			TotalCostTextView.setText(TotalCostText);
			
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
