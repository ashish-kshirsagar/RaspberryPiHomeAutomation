package com.example.accelerometersensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	final static String CONTEXT = "Context";	
	public static Context cont = null; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		cont = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public void startMonitorService(View v)
	{
		Intent intent = new Intent(getBaseContext(), MotionDetectIntentService.class);
		EditText et = (EditText)findViewById(R.id.ipaddress);		
		intent.putExtra(IP_ADDRESS, et.getText());
		et = (EditText)findViewById(R.id.portno);
		intent.putExtra(PORT_NO, et.getText());	
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);
	}
	
	public void stopMonitorService(View v)
	{
		stopService(new Intent(getBaseContext(), MotionDetectIntentService.class));
	}
}
