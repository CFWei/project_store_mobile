package com.example.takenumbersystemstore;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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
			
			LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View gridview;

			gridview=new View(context);
			gridview=layoutinflater.inflate(R.layout.item_view, null);
			TextView Now_Value=(TextView)gridview.findViewById(R.id.Now_Value);
			Now_Value.setText(item_list.get(arg0).get("Now_Value"));
			
			
			
			/*
			TextView State=(TextView)gridview.findViewById(R.id.State_Value);
			State.setText(item_list.get(arg0).get("State"));
			*/
			
			TextView Item_name=(TextView)gridview.findViewById(R.id.Item_Name);
			Item_name.setText(item_list.get(arg0).get("Name"));
			Log.v("debug", item_list.get(arg0).get("Name"));
			TextView hint=(TextView)gridview.findViewById(R.id.hint);
			hint.setText(item_list.get(arg0).get("hintcontent"));
			
			TextView takennumber=(TextView)gridview.findViewById(R.id.taken_number_value1);
			takennumber.setText(item_list.get(arg0).get("Value"));
			
			int watinum_value = Integer.parseInt(item_list.get(arg0).get("Value"))-Integer.parseInt(item_list.get(arg0).get("Now_Value"));
			TextView waitnum=(TextView)gridview.findViewById(R.id.WaitNum_Value);
			waitnum.setText(Integer.toString(watinum_value));
			
			
			return gridview;
	}

		
	@Override
	public void notifyDataSetChanged() {
		
		super.notifyDataSetChanged();
	}
	
	

}
