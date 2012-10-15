package com.example.takenumbersystemstore;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class Type2SettingPage extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type2_setting_page);
        
        Button EditStoreInformationButton=(Button)findViewById(R.id.EditStoreInformation); 
        EditStoreInformationButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Type2SettingPage.this,modify_store_information.class);
				
				Bundle bundle=new Bundle();
				bundle.putString("SerialNumbers",Type2Activity.SerialNumbers);
				intent.putExtras(bundle);
				
				startActivity(intent);
				
			}
		});
        
        Button settingItemListButton=(Button)findViewById(R.id.SettingItemList);
        settingItemListButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(Type2SettingPage.this,Type2ItemList.class);
				
				startActivity(intent);
				
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type2_setting_page, menu);
        return true;
    }

    
}
