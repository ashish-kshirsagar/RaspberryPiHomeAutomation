package com.example.remotehomeaccess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {	
	static String ipAddress = "128.82.5.23";
	static int portNo = 6788;

	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	
	final static String TAG = "RemoteAccess";
	
	final static int SETTINGS_REQUEST_ID = 101;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	public void performAction(View view)
	{
		Intent intent;
		
		switch(view.getId())
		{
			case R.id.btnStatus:
				intent = new Intent(this, StatusActivity.class);
				intent.putExtra(IP_ADDRESS, ipAddress);
				intent.putExtra(PORT_NO, portNo);
				startActivity(intent);
				break;
			case R.id.btnManage:
				intent = new Intent(this, LightsActivity.class);
				intent.putExtra(IP_ADDRESS, ipAddress);
				intent.putExtra(PORT_NO, portNo);
				startActivity(intent);				
				break;
			case R.id.btnSettings:
				intent = new Intent(this, SettingsActivity.class);
				intent.putExtra(IP_ADDRESS, ipAddress);
				intent.putExtra(PORT_NO, portNo);
				startActivityForResult(intent, SETTINGS_REQUEST_ID);
				break;	
			case R.id.btnHawkEye:
				intent = new Intent(this, HawkEyeActivity.class);
				intent.putExtra(IP_ADDRESS, ipAddress);
				intent.putExtra(PORT_NO, portNo);
				startActivity(intent);
				break;	
		}
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      if (requestCode == SETTINGS_REQUEST_ID) {
	          if (resultCode == RESULT_OK) {	        	  
	        	  ipAddress = data.getExtras().get(IP_ADDRESS).toString();
	        	  portNo = Integer.parseInt(data.getExtras().get(PORT_NO).toString());
	        	  
	        	  Log.d(TAG, "Returned IP address: " + ipAddress + "  Port No: " + portNo);
	          }
	      }
	}
}
