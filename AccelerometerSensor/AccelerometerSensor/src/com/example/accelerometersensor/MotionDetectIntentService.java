package com.example.accelerometersensor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MotionDetectIntentService extends IntentService implements SensorListener {
	final static String IP_ADDRESS = "IPAddress";
	final static String PORT_NO = "Port";
	
	static DoBackgroundTask task = null;
	
	static String ipAddress = "";
	static int portNo = 6788;

	final static String TAG = "MotionDetections";
	
	private Socket socket;
	final static String ACC_PHONE_STR = "ACC";
	final static String COMMAND = "MOTION";
	
	
	private static final int FORCE_THRESHOLD = 500;//350;
	  private static final int TIME_THRESHOLD = 100;
	  private static final int SHAKE_TIMEOUT = 500;
	  private static final int SHAKE_DURATION = 500;
	  private static final int SHAKE_COUNT = 5;
	
	  private SensorManager mSensorMgr;
	  private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
	  private long mLastTime;
	  private OnShakeListener mShakeListener;
	  private Context mContext;
	  private int mShakeCount = 0;
	  private long mLastShake;
	  private long mLastForce;
	  
	  final static int NOTIFY_DELAY = 60; 
	  static long lastCommandSendTime = 0;
	  
	  //private ShakeListener mShaker;
	  
	  
	/**
	 * Starts this service to perform action Foo with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startActionFoo(Context context, String param1,
			String param2) {	
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Starts this service to perform action Baz with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startActionBaz(Context context, String param1,
			String param2) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public MotionDetectIntentService() {
		super("MotionDetectIntentService");
		resume();
		//new MotionDetectIntentService(getApplicationContext());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			ipAddress = intent.getExtras().get(IP_ADDRESS).toString(); 
			portNo = Integer.parseInt(intent.getExtras().get(PORT_NO).toString());
		}
	}

	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionFoo(String param1, String param2) {
		// TODO: Handle action Foo
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Handle action Baz in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionBaz(String param1, String param2) {		
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, "Sensor Started Successfully", Toast.LENGTH_LONG).show();		
		
		ipAddress = intent.getExtras().get(IP_ADDRESS).toString(); 
		portNo = Integer.parseInt(intent.getExtras().get(PORT_NO).toString());
		
		return START_STICKY;
	}
	
	
	private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
		protected Long doInBackground(URL... urls) {
			while(true)
			{
				try
				{
					Log.d(TAG, "Sleeping for 120 seconds ....");
					Thread.sleep(120000);
				}
				catch(Exception e)
				{}
			}
		}
		
		protected void onProgressUpdate(Integer... progress) {			
		}
		
		protected void onPostExecute(Long result) {
			stopSelf();
		}
	}
	
	
	public interface OnShakeListener
	  {
	    public void onShake();
	  }

	  public MotionDetectIntentService(Context context) 
	  { 
		  super("MotionDetectIntentService");
		  mContext = context;
	      resume();
	  }

	  public void setOnShakeListener(OnShakeListener listener)
	  {
	    mShakeListener = listener;
	  }
	  

	  public void resume() {
		  mSensorMgr = (SensorManager)MainActivity.cont.getSystemService(Context.SENSOR_SERVICE);
	    if (mSensorMgr == null) {
	      throw new UnsupportedOperationException("Sensors not supported");
	    }
	    Log.d(TAG, "delay ---- " + SensorManager.SENSOR_DELAY_NORMAL);
	    boolean supported = mSensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_GAME);
	    Log.d(TAG, "is it supported  " + supported);
	    if (!supported) {
	      mSensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
	      throw new UnsupportedOperationException("Accelerometer not supported");
	    }
	  }

	  
	  public void pause() {
	    if (mSensorMgr != null) {
	      mSensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
	      mSensorMgr = null;
	    }
	  }

	  public void onAccuracyChanged(int sensor, int accuracy) { }

	  @SuppressLint("NewApi")
	public void onSensorChanged(int sensor, float[] values) 
	  {
	    if (sensor != SensorManager.SENSOR_ACCELEROMETER) 
	    	return;
	    
	    long now = System.currentTimeMillis();

	    if ((now - mLastForce) > SHAKE_TIMEOUT) {
	      mShakeCount = 0;
	    }

	    if ((now - mLastTime) > TIME_THRESHOLD) {
	      long diff = now - mLastTime;
	      float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
	      if (speed > FORCE_THRESHOLD) {
	        if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
	        	
	        	Log.d(TAG, "Entered inside shake listener 444 ");
	        	
	          mLastShake = now;
	          mShakeCount = 0;
	            
	            Log.d(TAG, "Entered inside shake listener");	                        
	            long curTimeSec = System.currentTimeMillis()/1000;				
	            
	            Log.d(TAG, "current time  " + curTimeSec + "  last time  " + lastCommandSendTime);
				//Send trigger to server about this action if not one sent in last
				//X time duration
				if((curTimeSec - lastCommandSendTime) >= NOTIFY_DELAY)
				{
					 lastCommandSendTime = curTimeSec;
					 Log.d(TAG, "Trying connection ....  before  " + ipAddress + " : " + portNo);
					 
					 RemoteHomeServerConnect t = new RemoteHomeServerConnect();
					 if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
						  t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					 }
					 else {
					      t.execute();
					 }
				}	            
	        }
	        mLastForce = now;
	      }
	      mLastTime = now;
	      mLastX = values[SensorManager.DATA_X];
	      mLastY = values[SensorManager.DATA_Y];
	      mLastZ = values[SensorManager.DATA_Z];
	    }
	  }
	  
	  private class RemoteHomeServerConnect extends AsyncTask<URL, Integer, Long> {
			protected Long doInBackground(URL... urls) {
				String input = "";
		    	 try 
		    	 {
		    		Log.d(TAG, "Trying connection ....  " + ipAddress + " : " + portNo);
					InetAddress serverAddr = InetAddress.getByName(ipAddress);
					socket = new Socket(serverAddr, portNo);
					Log.d(TAG, "socket created....");
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())), true);
					out.println(ACC_PHONE_STR+ "|" + COMMAND);
					Log.d(TAG, "send packet to server ....");
					
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					input = br.readLine();
					Log.d(TAG, "Received from server  ....  " + input);
					socket.close();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				return (long)1;
			}
			
			protected void onProgressUpdate(Integer... progress) {			
			}
			
			protected void onPostExecute(Long result) {
			}
		}
}
