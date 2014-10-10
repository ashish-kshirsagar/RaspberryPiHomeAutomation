package com.example.remotehomeaccess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	
	final static String TAG = "RemoteAccess";
	
	final static int SETTINGS_REQUEST_ID = 101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void updateSettings(View v)
	{
		Log.d(TAG, "Action clicked...");
		//Intent intent = new Intent(getBaseContext(), MainActivity.class);				
		//intent.putExtra(IP_ADDRESS, et.getText());		
		//intent.putExtra(PORT_NO, et.getText());			
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//startService(intent);
		
		Intent resultData = new Intent();
		EditText et = (EditText)findViewById(R.id.ipaddress);
		resultData.putExtra(IP_ADDRESS, et.getText());
		et = (EditText)findViewById(R.id.portno);
		resultData.putExtra(PORT_NO, et.getText());
		setResult(Activity.RESULT_OK, resultData);
		finish();
	}
}
