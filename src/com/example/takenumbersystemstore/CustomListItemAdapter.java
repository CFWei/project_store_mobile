package com.example.takenumbersystemstore;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomListItemAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<HashMap<String,String>> CustomList;
	
	public CustomListItemAdapter(Context mcontext,ArrayList<HashMap<String,String>> mCustomList)
	{
		context=mcontext;
		CustomList=mCustomList;
		
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
		 LayoutInflater layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View ListView=layoutinflater.inflate(R.layout.customlist, null);
		 
		 TextView CustomNumber=(TextView)ListView.findViewById(R.id.CustomNumber);
		 CustomNumber.setText(CustomList.get(arg0).get("number"));
		 
		 return ListView;
	}

}
