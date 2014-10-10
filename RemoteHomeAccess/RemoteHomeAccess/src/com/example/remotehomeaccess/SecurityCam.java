package com.example.remotehomeaccess;

import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SecurityCam extends Activity {

	private WebView webView;
	private Socket socket;
	private static final int SERVERPORT = 6788;
	private static final String SERVER_IP = "128.82.5.23";
	
	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	final static String CAM_ID = "CamID";
	
	static String ipAddress = SERVER_IP;
	static int portNo = SERVERPORT;
	
	final static String TAG = "RemoteAccess";
		
	static int camid = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_cam);
		
		Bundle extras = getIntent().getExtras();		
		if(extras !=null)
		{
			ipAddress = getIntent().getExtras().get(IP_ADDRESS).toString();
			portNo = Integer.parseInt(getIntent().getExtras().get(PORT_NO).toString());
			camid = Integer.parseInt(getIntent().getExtras().get(CAM_ID).toString());
			
			Log.d(TAG, "IP address: " + ipAddress + "  Port No: " + portNo + " Camid : " + camid);
		}
		
		webView = (WebView) findViewById(R.id.camview);
		
		
		String url = "http://" + ipAddress + "/data/camShow.php?id=" + camid;
		Log.d(TAG, "calling url " + url);
		
		WebSettings webSettings = webView.getSettings();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginState(WebSettings.PluginState.ON);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
				
		webView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.security_cam, menu);
		return true;
	}

	public void onPause() {
	    super.onPause();
	    webView.stopLoading();
	    finish();
	}
	
	@Override
	public void onBackPressed() {
		 webView.stopLoading();
		 Log.d(TAG, "Back button pressed ");
		 finish();
	}
}
