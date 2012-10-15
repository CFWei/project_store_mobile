package com.example.takenumbersystemstore;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomItemAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<HashMap<String,String>> CustomItemList;
	
	public CustomItemAdapter(Context mcontext,ArrayList<HashMap<String,String>> mCustomItemList)
	{
		this.context=mcontext;
		this.CustomItemList=mCustomItemList;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return CustomItemList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 
		LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View ListView=layoutinflater.inflate(R.layout.customitemview, null);
		 
		TextView CustomItemName=(TextView)ListView.findViewById(R.id.CustomItemName);
		CustomItemName.setText(CustomItemList.get(position).get("ItemName"));
		
		TextView CustomItemCount=(TextView)ListView.findViewById(R.id.CustomItemCount);
		CustomItemCount.setText(CustomItemList.get(position).get("NeedValue"));
		
		TextView ItemEachPrice=(TextView)ListView.findViewById(R.id.ItemEachPrice);
		ItemEachPrice.setText(CustomItemList.get(position).get("Price"));
		
		
		return ListView;
	}
	
	
	
}
