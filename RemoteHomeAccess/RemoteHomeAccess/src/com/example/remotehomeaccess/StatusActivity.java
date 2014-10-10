package com.example.remotehomeaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StatusActivity extends Activity {

	private Socket socket;
	private static final int SERVERPORT = 6788;
	private static final String SERVER_IP = "128.82.5.23";//"128.82.5.53";
	
	String str = "Hello ";
	static boolean systemStatus = false;
	static boolean notifyStatus = false;
	
	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	
	static String ipAddress = SERVER_IP;
	static int portNo = SERVERPORT;
	
	final static String TAG = "RemoteAccess";
	
	final static String MASTER_PHONE_STR = "MASTER";
	final static String SYSTEM_CMD_STR = "SYSTEM";
	final static String TOGGLE_STATUS_CMD = "TOGGLE";
	final static String GET_STATUS_CMD = "GET";
	
	final static String STATUS_OFF_STR = "OFF";
	final static String STATUS_ON_STR = "ON";
	
	final static String SYSTEM_SETTINGS = "SYSTEM";
	final static String NOTIFY_SETTINGS = "NOTIFY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		Bundle extras = getIntent().getExtras();		
		if(extras !=null)
		{
			ipAddress = getIntent().getExtras().get(IP_ADDRESS).toString();
			portNo = Integer.parseInt(getIntent().getExtras().get(PORT_NO).toString());
			
			Log.d(TAG, "IP address: " + ipAddress + "  Port No: " + portNo);
		}
		
		new RemoteHomeService().execute(MASTER_PHONE_STR, SYSTEM_CMD_STR, GET_STATUS_CMD, SYSTEM_SETTINGS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true;
	}
	
	
	//Toggle the device status
	public void sendCommand(View view)
	{
		switch(view.getId())
		{
			case R.id.tbtnSysSecurity:
				Log.d("TCP", "Called toggle security settings ....");
				new RemoteHomeService().execute(MASTER_PHONE_STR, SYSTEM_CMD_STR, TOGGLE_STATUS_CMD, SYSTEM_SETTINGS);				
				break;
			case R.id.tbtnNotifySetting:
				Log.d("TCP", "Called toggle notification settings ....");
				new RemoteHomeService().execute(MASTER_PHONE_STR, SYSTEM_CMD_STR, TOGGLE_STATUS_CMD, NOTIFY_SETTINGS);
				break;
		}
	}
	
	private class RemoteHomeService extends AsyncTask<String, Integer, String> {
	     protected String doInBackground(String... command) {
	    	 String input = "";
	    	 Log.d("TCP", "Thread called ...");
	    	 try 
	    	 {
	    		Log.d("TCP", "Connecting to server ....");
				InetAddress serverAddr = InetAddress.getByName(ipAddress);
				socket = new Socket(serverAddr, portNo);
				Log.d("TCP", "socket created....");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				//send message is --- Lamp|GET|11
				out.println(command[0]+"|"+command[1]+"|"+command[2]+"|"+command[3]);
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
	    	 
	         //showDialog("Downloaded " + result + " bytes");
	    	 //Toast.makeText(getBaseContext(),"From Server: " + result, Toast.LENGTH_LONG).show();
	    	 String system = "" , notify = ""; 
	    	 
	    	 StringTokenizer st = new StringTokenizer(result, "|");
	    	 /*while (st.hasMoreTokens()) {
	    	     System.err.println(st.nextToken());
	    	 }*/
	    	 
	    	 system = st.nextToken();
	    	 notify = st.nextToken();
	    	 
	    	 
	    	 if(system.endsWith(STATUS_ON_STR))	    	  
	    		 systemStatus = true;
	    	 else
	    	 	 systemStatus = false;	    	 
	    	 
	    	 if(notify.endsWith(STATUS_ON_STR))	    	  
	    		 notifyStatus = true;
	    	 else
	    		 notifyStatus = false;	
	    	 
	    	 ToggleButton tb = (ToggleButton)findViewById(R.id.tbtnSysSecurity);
	    	 tb.setChecked(systemStatus);
	    	 tb = (ToggleButton)findViewById(R.id.tbtnNotifySetting);
	    	 tb.setChecked(notifyStatus);
	     }
	}
}
