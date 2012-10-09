package com.tabbie.android.radar;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.tabbie.android.radar.enums.MessageType;
import com.tabbie.android.radar.http.GenericServerPutRequest;
import com.tabbie.android.radar.http.ServerThreadHandler;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService implements Handler.Callback {
	public static final String TAG = "GCMIntentService";
	
	public GCMIntentService() {
		super("486514846150");
		Log.d(TAG, "Default Constructor");
	}
	
	public GCMIntentService(String... senderID) {
		super(senderID);
		Log.d(TAG, "String Parameter Constructor");
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onError");
	}
	
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRecoverableError");
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		

	   
    // ########################### Sample Code for Notifications 
    /*
    Notification notification = new Notification(R.drawable.ic_launcher, "Hey. Hey you. Fuck you", System.currentTimeMillis());
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, EventDetailsActivity.class), 0);
    notification.setLatestEventInfo(this, "Tabbie Push Notification", "Hey Cesar, Go Fuck Yourself", contentIntent);
    NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNM.notify(R.string.age_title, notification);
    */
		
		Log.d(TAG, "onMessage");
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		
		String token = context.getSharedPreferences("MainActivity", MODE_PRIVATE).getString("token", null);
		token = "MAS7Z7XyfQyECGJgzoz5";
		HandlerThread registerThread = new HandlerThread("GCM Thread");
		registerThread.start();
		GenericServerPutRequest req = new GenericServerPutRequest(MessageType.REGISTER_GCM, regId, token);
		// req.params.put("auth_token", token);
	  req.responseHandler = new Handler(this);
		ServerThreadHandler upstreamHandler = new ServerThreadHandler(registerThread.getLooper());
	  final Message message = Message.obtain();
	  message.obj = req;
	  req.responseHandler = new Handler(this);
	  upstreamHandler.sendMessage(message);
		// TODO Auto-generated method stub
		Log.d(TAG, "onRegistered");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onUnregistered");
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
}