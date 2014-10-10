package com.example.remotehomeaccess;

import java.net.Socket;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;

public class HawkEyeActivity extends Activity {

	private Socket socket;
	private static final int SERVERPORT = 6788;
	private static final String SERVER_IP = "128.82.5.23";
	
	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	final static String CAM_ID = "CamID";
	
	static String ipAddress = SERVER_IP;
	static int portNo = SERVERPORT;
	
	
	final static String TAG = "RemoteAccess";
	
	private WebView mWebView;
	
	final static int LIVING_ROOM_CAM_UID = 32;
	final static int KITCHEN_CAM_UID = 31;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hawk_eye);
		
		Bundle extras = getIntent().getExtras();		
		if(extras !=null)
		{
			ipAddress = getIntent().getExtras().get(IP_ADDRESS).toString();
			portNo = Integer.parseInt(getIntent().getExtras().get(PORT_NO).toString());
			
			Log.d(TAG, "IP address: " + ipAddress + "  Port No: " + portNo);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hawk_eye, menu);
		return true;
	}

	public void startCamView(View v)
	{	
		int camid = 0;
		switch(v.getId())
		{
			case R.id.kitchencam:
				camid = KITCHEN_CAM_UID;
				break;
			case R.id.livroomcam:
				camid = LIVING_ROOM_CAM_UID;
				break;
		}
		
		
		Intent intent = new Intent(getApplicationContext(), SecurityCam.class);
		intent.putExtra(IP_ADDRESS, ipAddress);
		intent.putExtra(PORT_NO, portNo);
		intent.putExtra(CAM_ID, camid);
	    startActivity(intent);
	}
}
