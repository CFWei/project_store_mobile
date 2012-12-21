package com.example.takenumbersystemstore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import com.example.takenumbersystemstore.ManageActivity.ImplementItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class ItemAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<HashMap<String,String>> item_list;
	public ItemAdapter(Context mcontext,ArrayList<HashMap<String,String>> mitem_list)
	{
		context=mcontext;
		this.item_list=mitem_list;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return item_list.size();
	}
	public void setdata(ArrayList<HashMap<String,String>> mitem_list)
	{
		item_list=mitem_list;
		
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
			final int WhichItem=arg0;
			final LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View gridview;

			gridview=layoutinflater.inflate(R.layout.item_view, null);
			TextView Now_Value=(TextView)gridview.findViewById(R.id.Now_Value);
			Now_Value.setText(item_list.get(arg0).get("Now_Value"));
			
			
			
			LinearLayout ItemLayoutMain=(LinearLayout)gridview.findViewById(R.id.ItemLayoutMain);
		
			
			
			ItemLayoutMain.setOnLongClickListener(new OnLongClickListener() {
				
				public boolean onLongClick(View arg) {
					//CharSequence[] content={"此商品全螢幕","修改商品資訊","刪除此商品"};
					CharSequence[] content={"刪除此商品"};
					Builder MyAlertDialog = new AlertDialog.Builder(context);
					MyAlertDialog.setTitle("商品選單");
					MyAlertDialog.setItems(content, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
							Bundle bundle=new Bundle();
							String ID=item_list.get(WhichItem).get("ID");
							switch(which)
							{	
								/*
								case 0:
									intent.setClass(context, itemfullscreen.class);
									bundle.putString("SerialNumbers",ManageActivity.SerialNumbers);
									bundle.putString("ID",ID);
									intent.putExtras(bundle);
									context.startActivity(intent);
									break;
								case 1:
									intent.setClass(context, edititem.class);
									bundle.putString("SerialNumbers",ManageActivity.SerialNumbers);
									bundle.putString("ID",ID);
									intent.putExtras(bundle);
									context.startActivity(intent);
									break;
									*/
								case 0:
									ManageActivity.ImplementItem delete_item_runnable=new ManageActivity.ImplementItem();
									delete_item_runnable.setdata(1,WhichItem);
									Thread delete_item_thread=new Thread(delete_item_runnable);
									delete_item_thread.start();
									break;	
								case 1:
									ManageActivity.ImplementItem FullScreen=new ManageActivity.ImplementItem();
									FullScreen.setdata(3, WhichItem);
									Thread fullScreenThread=new Thread(FullScreen);
									fullScreenThread.start();
									break;
							}
							
						}
					});
					 AlertDialog alert = MyAlertDialog.create();
					 alert.show();
					
					 
					return false;
				}
			});
			
			
			/*
			TextView State=(TextView)gridview.findViewById(R.id.State_Value);
			State.setText(item_list.get(arg0).get("State"));
			*/
			
			TextView Item_name=(TextView)gridview.findViewById(R.id.Item_Name);
			Item_name.setText(item_list.get(arg0).get("Name"));
			TextView hint=(TextView)gridview.findViewById(R.id.hint);
			hint.setText(item_list.get(arg0).get("hintcontent"));
			
			TextView takennumber=(TextView)gridview.findViewById(R.id.taken_number_value1);
			takennumber.setText(item_list.get(arg0).get("Value"));
			
			
			//輸入數字視窗的按鈕
			Button InputNumberButton=(Button)gridview.findViewById(R.id.InputNumberButton);
			InputNumberButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					final View InputNumberView=layoutinflater.inflate(R.layout.inputnumberview, null);
					new AlertDialog.Builder(context).setTitle("輸入號碼").setView(InputNumberView)
					.setPositiveButton("確定", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							EditText inputnumber=(EditText)InputNumberView.findViewById(R.id.InputNumberEditText);
							
							String editvalue=inputnumber.getText().toString();
							String ID=item_list.get(WhichItem).get("ID");
							
							ManageActivity.EditNowValue editnowvalue=new ManageActivity.EditNowValue();
							editnowvalue.setData(ID, editvalue,WhichItem);
							
							Thread ImplementEditNowValueThread=new Thread(editnowvalue);
							ImplementEditNowValueThread.start();
							
						}
					})
					.setNegativeButton("取消", null)
					.show();
					
				}
			});
			
			
			Button LookUpClientButton=(Button)gridview.findViewById(R.id.LookUpClientButton);
			LookUpClientButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					 View LookUpCustomView=layoutinflater.inflate(R.layout.lookupcustom, null);
					 
					 String ID=item_list.get(WhichItem).get("ID");
					 ManageActivity.GetCustomInformation getcustominformation= new ManageActivity.GetCustomInformation();
					 getcustominformation.setData(ID);
					 
					 Thread GetCustomInformatinThread=new Thread(getcustominformation);
					 GetCustomInformatinThread.start();
					 
					
					
				}
			});
			
			
			Button Next_Value=(Button)gridview.findViewById(R.id.NextValue);
			Next_Value.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) 
				{
					ManageActivity.NextValue nextvalue=new ManageActivity.NextValue();
					nextvalue.setData(WhichItem,"1");
					Thread NextValueThread=new Thread(nextvalue);
					NextValueThread.start();
				}
			});
			
			Button SkipValue=(Button)gridview.findViewById(R.id.SkipValue);
			SkipValue.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					ManageActivity.NextValue nextvalue=new ManageActivity.NextValue();
					nextvalue.setData(WhichItem,"2");
					Thread NextValueThread=new Thread(nextvalue);
					NextValueThread.start();
					
				}
			});
			
			
			//int watinum_value = Integer.parseInt(item_list.get(arg0).get("Value"))-Integer.parseInt(item_list.get(arg0).get("Now_Value"));
			TextView waitnum=(TextView)gridview.findViewById(R.id.WaitNum_Value);
			waitnum.setText(item_list.get(arg0).get("waitcustomvalue"));
			
			
			return gridview;
	}
	

	@Override
	public void notifyDataSetChanged() {
		
		super.notifyDataSetChanged();
	}
	
	

			
			
	

}
