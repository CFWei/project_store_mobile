package com.example.takenumbersystemstore;

import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.support.v4.app.NavUtils;

public class Type1Setting extends Activity {
	private String SerialNumbers;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type1_setting);
        
        Intent thisIntent = this.getIntent();
        Bundle bundle=thisIntent.getExtras();
        SerialNumbers=bundle.getString("SerialNumbers");
        
        setButtonListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type1_setting, menu);
        return true;
    }
    
    @Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		
	}

	public void setButtonListener(){
    	
		Button logout = (Button) findViewById(R.id.LogoutButton);
		logout.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i=new Intent();
				Bundle b=new Bundle();
				b.putString("Action","Logout");
				i.putExtras(b);
				
				setResult(333,i);
				Type1Setting.this.finish();
				
				
			}
		});
		
		Button AddNewItemButton = (Button) findViewById(R.id.AddNewItemButton);
		AddNewItemButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Type1Setting.this, additem.class);

				Bundle bundle = new Bundle();
				bundle.putString("SerialNumbers", SerialNumbers);
				intent.putExtras(bundle);

				startActivity(intent);
			}
		});


		
		Button ModifyStoreInformationButton = (Button) findViewById(R.id.ModifyStoreInformationButton);
		ModifyStoreInformationButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Type1Setting.this,
						modify_store_information.class);

				Bundle bundle = new Bundle();
				bundle.putString("SerialNumbers", SerialNumbers);
				intent.putExtras(bundle);

				startActivity(intent);

			}
		});
		
    	
    }
    
}
