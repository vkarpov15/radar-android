package com.tabbie.android.radar;

import android.content.Context;
import android.content.Intent;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService {

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// TODO Auto-generated method stub
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}