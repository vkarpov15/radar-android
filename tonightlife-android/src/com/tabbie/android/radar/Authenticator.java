package com.tabbie.android.radar;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.tabbie.android.radar.core.DebugUtils;
import com.tabbie.android.radar.http.ServerGetRequest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Authenticator {
	public static final String TAG = "Authenticator";
	private final Context context;
	private String fbAccessToken;
	private long fbExpires;
	
	public Authenticator(final Context context) {
		this.context = context;
	}
	
	public void authenticateFacebook(final String appId) {
		
	}
	
	public void authenticateFacebook(final String sharedPref, final String appId) {
		final SharedPreferences prefs = context.getSharedPreferences(sharedPref, Context.MODE_PRIVATE);
	}
	
	public void authenticateFacebook(final String sharedPref, final int accessMode, final String appId) {
		final Facebook facebook = new Facebook(appId);
		final SharedPreferences prefs = context.getSharedPreferences(sharedPref, accessMode);
    fbAccessToken = prefs.getString("access_token", null);
    fbExpires = prefs.getLong("expires", 0);
    if (fbAccessToken != null) {
      facebook.setAccessToken(fbAccessToken);
    }
    if (fbExpires != 0) {
      facebook.setAccessExpires(fbExpires);
    }
    if (facebook.isSessionValid()) {
      if(DebugUtils.DEBUG) Log.i(TAG, "Session is valid");
      final ServerGetRequest req = new ServerGetRequest(
          "https://graph.facebook.com/me/?access_token="
              + facebook.getAccessToken(), MessageType.FACEBOOK_LOGIN);
    	req.responseHandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			// TODO Auto-generated method stub
    			super.handleMessage(msg);
    		}
    	};
      final Message message = Message.obtain();
      message.obj = req;
      final Thread requestThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
			});
      // upstreamHandler.sendMessage(message);
    } else {
      facebook.authorize((Activity) context, new String[] { "email" }, new DialogListener() {
        public void onComplete(final Bundle values) {
        	
        }

        public void onFacebookError(final FacebookError e) {
          Log.e(TAG, "Facebook Error");
        }

        public void onError(final DialogError e) {
          Log.e(TAG, "DialogError");
        }

        public void onCancel() {
          Log.i(TAG, "Facebook Login Canceled");
        }
      });
    }
	}
}