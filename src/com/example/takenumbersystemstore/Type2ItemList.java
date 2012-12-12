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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Type2ItemList extends Activity {
	public ArrayList<HashMap<String,String>> ItemList;
	private Handler mhandler;
	private Type2ItemListAdapter TIA;
	private String SerialNumbers;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type2_item_list);
        
        SerialNumbers=this.getIntent().getExtras().getString("SerialNumbers");
        
        mhandler=new Handler(){
    		public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			switch (msg.what)
    			{	
    				case 1:
    					TIA=new Type2ItemListAdapter(Type2ItemList.this, ItemList);
    					ListView ItemListView=(ListView)findViewById(R.id.ItemListView);
    					ItemListView.setAdapter(TIA);
    					break;
    				case 2:
    					String MsgString = (String)msg.obj;
    					Toast.makeText(Type2ItemList.this, MsgString, Toast.LENGTH_SHORT).show();
    					break;
    				case 3:
    					TIA.notifyDataSetChanged();
    					break;
    			
    			}
    		}
    		
    	};
        
    	Button AddItemSubmitButton=(Button)findViewById(R.id.AddItemSubmit);
    	AddItemSubmitButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) 
			{	
				
				Builder MyAlertDialog = new AlertDialog.Builder(Type2ItemList.this);
				MyAlertDialog.setTitle("確認視窗");
				MyAlertDialog.setMessage("確定要新增?");
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						TextView ItemNameTextView=(TextView)findViewById(R.id.ItemNameValue);
						String ItemName=ItemNameTextView.getText().toString();
						
						TextView ItemPriceValue=(TextView)findViewById(R.id.ItemPriceValue);
						String ItemPrice=ItemPriceValue.getText().toString();
						
						//AddItem AI=new AddItem(ItemName,ItemPrice);
						//Thread AddItemThread=new Thread(AI);
						//AddItemThread.start();
					}
				};
				MyAlertDialog.setNeutralButton("確認",OkClick );
				MyAlertDialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				MyAlertDialog.show();
				
				
			}
		});
    	
        
        Thread GetListThread=new Thread(GetList);
        GetListThread.start();
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_type2_item_list, menu);
        return true;
    }
    
    class AddItem implements Runnable
    {
    	String ItemName,ItemPrice;
    	
    	public AddItem(String ItemName,String ItemPrice)
    	{
    		this.ItemName=ItemName;
    		this.ItemPrice=ItemPrice;
    			
    		
    	}
    	
		public void run() 
		{
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				nameValuePairs.add(new BasicNameValuePair("ItemName",ItemName));
				nameValuePairs.add(new BasicNameValuePair("ItemPrice",ItemPrice));
				String result=connect_to_server("project/store/Type2/AddItem.php",nameValuePairs);
				Log.v("debug", result);
				if(result.equals("0"))
				{
					Message m=mhandler.obtainMessage(2,"新增成功");
					mhandler.sendMessage(m);

					//更新列表
					Thread GetListThread=new Thread(GetList);
				    GetListThread.start();
				        
				        
					
				}
				else
				{
					Message m=mhandler.obtainMessage(2,"新增失敗");
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
    
    Runnable GetList=new Runnable() 
    {
		
		public void run() 
		{
			try 
			{
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",SerialNumbers));
				String result=connect_to_server("project/store/Type2/GetItemList.php",nameValuePairs);
				String key[]={"ItemName","Price"};
				ItemList=json_deconde(result,key);
				
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
    
    
    class Type2ItemListAdapter extends BaseAdapter
    {	
    	
    	private Context context;
    	private ArrayList<HashMap<String,String>> ItemList;
    	public Type2ItemListAdapter(Context context,ArrayList<HashMap<String,String>> ItemList)
    	{
    		this.context=context;
    		this.ItemList=ItemList;
    		
    	}
    	
    	
		public int getCount() 
		{
		
			return ItemList.size();
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
			
			LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View ListView=layoutinflater.inflate(R.layout.type2item, null);
			
			TextView ItemNameTextView=(TextView)ListView.findViewById(R.id.ItemNameValue);
			ItemNameTextView.setText(ItemList.get(arg0).get("ItemName"));
			
			TextView ItemPriceTextView=(TextView)ListView.findViewById(R.id.ItemPriceValue);
			ItemPriceTextView.setText(ItemList.get(arg0).get("Price"));
			
			final int position=arg0;
			
			Button DeleteButton=(Button)ListView.findViewById(R.id.DeleteButton);
			DeleteButton.setOnClickListener(new Button.OnClickListener() 
			{
				
				public void onClick(View v) 
				{
					Builder MyAlertDialog = new AlertDialog.Builder(Type2ItemList.this);
					MyAlertDialog.setTitle("確認視窗");
					MyAlertDialog.setMessage("確定要刪除?");
					MyAlertDialog.setPositiveButton("確定", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) 
						{
							String ItemName=ItemList.get(position).get("ItemName");
							DeleteItem DI=new DeleteItem(ItemName);
							Thread DIThread=new Thread(DI);
							DIThread.start();
							
						}
					});
					
					
					MyAlertDialog.setNegativeButton("取消", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					MyAlertDialog.show();
					
				}
			});
			
			
			return ListView;
		}
    	
    	
    	
    }
    
    
    class DeleteItem implements Runnable
    {	
    	String ItemName;
    	public DeleteItem(String ItemName)
    	{
    		this.ItemName=ItemName;
    	}
    	
    	
		public void run() 
		{
			
			try {
				ArrayList<NameValuePair> nameValuePairs =new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("SerialNumbers",Type2Activity.SerialNumbers));
				nameValuePairs.add(new BasicNameValuePair("ItemName",ItemName));
				String result=connect_to_server("project/store/Type2/DeleteItem.php",nameValuePairs);
				
				if(result.equals("0"))
				{	
					Message m=mhandler.obtainMessage(2,"刪除成功");
					mhandler.sendMessage(m);
					
					Thread GetListThread=new Thread(GetList);
				    GetListThread.start();
					
					
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
    
    public static String connect_to_server(String program,ArrayList<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException
    {	
    	//建立一個httpclient
    	HttpClient httpclient = new DefaultHttpClient();
    	//設定httppost的網址
    	HttpPost httppost = new HttpPost(MainActivity.ServerURL+program);
    	
    	//加入參數
    	if(nameValuePairs!=null)
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
    	
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
