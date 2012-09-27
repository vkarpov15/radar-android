package com.tabbie.android.radar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService {
	public static final String TAG = "GCMIntentService";

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
		Log.d(TAG, "onMessage");
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRegistered");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onUnregistered");
	}
}