package com.tabbie.android.radar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService {
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
		/*
		Log.d(TAG, "Received keys: " + data.getExtras().keySet());
		for(String key : data.getExtras().keySet()) {
			Log.v(TAG, "Key " + key + " is: " + data.getExtras().getString(key));
		}
		Log.d(TAG, "onMessage");
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("event_id", "TEST");
		resultIntent.putExtra("user_name", "TEST TEST");
		resultIntent.putExtra("message", "TEST TEST TEST TEST TEST TEST TEST");
		resultIntent.setClass(this, MainActivity.class);
		
		NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setAutoCancel(true);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(42, mBuilder.getNotification());*/
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		/*
		Intent register = new Intent(ACTION_REGISTER_GCM);
		register.putExtra("regId", regId);
		LocalBroadcastManager.getInstance(context).sendBroadcast(register);
		Log.d(TAG, "onRegistered");
		*/
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
		Log.d(TAG, "onUnregistered");
	}
}