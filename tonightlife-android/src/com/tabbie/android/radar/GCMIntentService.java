package com.tabbie.android.radar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService implements Handler.Callback {
	public static final String TAG = "GCMIntentService";
	public static final String ACTION_REGISTER_GCM = "RegisterGCM";
	
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
	protected void onMessage(Context context, Intent data) {
		// TODO Debug
    Notification notification = new Notification(R.drawable.ic_launcher, "Hey. Hey you. Fuck you", System.currentTimeMillis());
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
            new Intent(this, EventDetailsActivity.class), 0);
    notification.setLatestEventInfo(this, "Tabbie Push Notification", "Hey Cesar, Go Fuck Yourself", contentIntent);
    NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNM.notify(42, notification);
    
		Log.d(TAG, "Received keys: " + data.getExtras().keySet());
		for(String key : data.getExtras().keySet()) {
			Log.v(TAG, "Key " + key + " is: " + data.getExtras().getString(key));
		}
		Log.d(TAG, "onMessage");
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		Intent register = new Intent(ACTION_REGISTER_GCM);
		register.putExtra("regId", regId);
		LocalBroadcastManager.getInstance(context).sendBroadcast(register);
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