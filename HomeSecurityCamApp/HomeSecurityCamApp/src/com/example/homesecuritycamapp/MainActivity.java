package com.example.homesecuritycamapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int SERVERPORT = 6788;
	private static final String SERVER_IP = "128.82.5.23";
	
	static String ipAddress = SERVER_IP;
	static int portNo = SERVERPORT;
	
	private Socket socket;
	
	final static String CAM_PHONE_STR = "SECURITYCAM";
	
	final static String LIVING_ROOM_CAM_STR = "Living Room";
	final static String KITCHEN_CAM_STR = "Kitchen";
	
	final static int LIVING_ROOM_CAM_UID = 32;
	final static int KITCHEN_CAM_UID = 31;
	
	final static String STATUS_OFF_STR = "OFF";
	final static String STATUS_ON_STR = "ON";
	
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

	public void startCam(View v)
	{
		TextView tv = (TextView) findViewById(R.id.ipaddress);
		ipAddress = tv.getText().toString();
		tv = (TextView) findViewById(R.id.portno);
		portNo = Integer.parseInt(tv.getText().toString());
		
		/*WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		@SuppressWarnings("deprecation")
		String ipAddressLocal = Formatter.formatIpAddress(ip);*/
		
		String ipAddressLocal = wifiIpAddress(getApplicationContext());
		
		
		int camuid = 0;
		
		tv = (TextView) findViewById(R.id.location);
		String str = tv.getText().toString();
		
		//Decide which is the camera uid 
		if(str.equalsIgnoreCase(LIVING_ROOM_CAM_STR))
			camuid = LIVING_ROOM_CAM_UID;
		else
			camuid = KITCHEN_CAM_UID;
		
		Log.d("RemoteCam", "Device IP address : " + ipAddressLocal + "  UID: " + camuid);
		
		new RemoteHomeService().execute(CAM_PHONE_STR, ""+camuid, ipAddressLocal);
		
		//Intent intent = getPackageManager().getLaunchIntentForPackage("teaonly.projects.droidipcam");
		//startActivity(intent);
		//finish();
	}
	
	
	protected String wifiIpAddress(Context context) {
	    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

	    // Convert little-endian to big-endianif needed
	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	        ipAddress = Integer.reverseBytes(ipAddress);
	    }

	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

	    String ipAddressString;
	    try {
	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException ex) {
	        Log.e("WIFIIP", "Unable to get host address.");
	        ipAddressString = null;
	    }

	    return ipAddressString;
	}
	
	
	private class RemoteHomeService extends AsyncTask<String, Integer, String> {
	     protected String doInBackground(String... command) {
	    	 String input = "";
	    	 try 
	    	 {
				InetAddress serverAddr = InetAddress.getByName(ipAddress);
				socket = new Socket(serverAddr, portNo);
				Log.d("TCP", "socket created....");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				//send message is --- Lamp|GET|11
				out.println(command[0]+"|"+command[1]+"|"+command[2]);
				Log.d("TCP", "send packet to server ....");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				input = br.readLine();
				Log.d("TCP", "Received from server  ....  " + input);
				socket.close();
			} catch (UnknownHostException e1) {
				input = "";
				e1.printStackTrace();
			} catch (IOException e1) {
				input = "";
				e1.printStackTrace();
			}
			
	        return input;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	    	 if(result  == null || result.equals(""))
	    	 {
	    		 Toast.makeText(getBaseContext(),"Service unavailable !!!", Toast.LENGTH_LONG).show();
	    		 return;
	    	 }
	    	 
	    	 if(result.endsWith(STATUS_ON_STR))
	    	 {
	    		 //Call camera app to start cam.
	    		 Intent intent = getPackageManager().getLaunchIntentForPackage("teaonly.projects.droidipcam");
	    		 startActivity(intent);
	    		 finish();
	    	 }
	    	 else
	    	 {
	    		 Toast.makeText(getBaseContext(),"Service unavailable !!!", Toast.LENGTH_LONG).show();
	    		 return;
	    	 }
	     }
	}
}
