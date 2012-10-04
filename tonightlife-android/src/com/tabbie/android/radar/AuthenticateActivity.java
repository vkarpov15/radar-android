package com.tabbie.android.radar;

/**
 *  AuthenticateActivity.java
 * 
 *  Created on: August 17, 2012
 *      Author: Justin Knutson
 * 
 * 
 * Launch this activity to authenticate
 * against facebook and tabbie, returning
 * the login info (name, token, etc.) to
 * the calling activity so it can request
 * updates from the tabbie server
 */

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.apps.analytics.easytracking.TrackedActivity;
import com.tabbie.android.radar.http.ServerGetRequest;
import com.tabbie.android.radar.http.ServerPostRequest;
import com.tabbie.android.radar.http.ServerResponse;

public class AuthenticateActivity extends TrackedActivity implements Handler.Callback {
  public final String TAG = "Authenticate Activity";
  private final Handler upstreamHandler;
  
  private final Facebook facebook = new Facebook("217386331697217");
  
  private String fbAccessToken = null;
  private String tabbieAccessToken = null;
  private String facebookName = null;
  private long expires = 0;

  public AuthenticateActivity() {
	  super();
	  final HandlerThread serverThread = new HandlerThread(TAG + "Thread");
	  serverThread.start();
	  upstreamHandler = new ServerThreadHandler(serverThread.getLooper());
  }
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.authenticate_background);
    ((ImageView) findViewById(R.id.loading_spin)).startAnimation(AnimationUtils
        .loadAnimation(this, R.anim.rotate));

    final Intent data = this.getIntent();

    fbAccessToken = data.getStringExtra("token");
    expires = data.getLongExtra("expires", 0);

    if (fbAccessToken != null) {
      facebook.setAccessToken(fbAccessToken);
    }

    if (expires != 0) {
      facebook.setAccessExpires(expires);
    }

    if (facebook.isSessionValid()) {
      Log.i(TAG, "Session is valid");
      final ServerGetRequest req = new ServerGetRequest(
          "https://graph.facebook.com/me/?access_token="
              + facebook.getAccessToken(), MessageType.FACEBOOK_LOGIN);
    	req.responseHandler = new Handler(this);
      final Message message = Message.obtain();
      message.obj = req;
      upstreamHandler.sendMessage(message);
    } else {
      facebook.authorize(this, new String[] { "email" }, new DialogListener() {
        public void onComplete(final Bundle values) {
            final ServerGetRequest req = new ServerGetRequest(
                    "https://graph.facebook.com/me/?access_token="
                        + facebook.getAccessToken(), MessageType.FACEBOOK_LOGIN);
          	req.responseHandler = new Handler(AuthenticateActivity.this);
            final Message message = Message.obtain();
            message.obj = req;
            upstreamHandler.sendMessage(message);
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    facebook.authorizeCallback(requestCode, resultCode, data);
  }

	@Override
	public boolean handleMessage(Message msg) {
		Log.d(TAG, "Received message response");
		if(!(msg.obj instanceof ServerResponse)) {
			Log.e(TAG, "Message is not a Server Response");
			return false;
		}
		final ServerResponse resp = (ServerResponse) msg.obj;
	    final JSONObject json = resp.parseJsonContent();
	    if (null == json) {
	      Log.i(TAG, "JSON Response is null");
	      return false;
	    }
	    switch (resp.responseTo) {
	    case FACEBOOK_LOGIN:
	      if (json.has("id")) {
	        try {
	          facebookName = json.getString("first_name") + " "
	              + json.getString("last_name").substring(0, 1) + ".";
	        } catch (final JSONException e) {
	          e.printStackTrace();
	          return false;
	        }
	        final ServerPostRequest req = new ServerPostRequest(
	            getString(R.string.tabbie_server) + "/mobile/auth.json",
	            MessageType.TABBIE_LOGIN);
	
	        		req.params.put("fb_token", facebook.getAccessToken());
	        		req.responseHandler = new Handler(this);
	          	final Message message = Message.obtain();
	          	message.obj = req;
	          	upstreamHandler.sendMessage(message);
	      } else {
	    	Log.e(TAG, "Facebook Log-in JSON does not have an ID!");
	    	throw new RuntimeException();
	      }
	      break;
	    case TABBIE_LOGIN:
	      if (json.has("token")) {
	        try {
	          tabbieAccessToken = json.getString("token");
	        } catch (final JSONException e) {
	          e.printStackTrace();
	          return false;
	        } finally {
	          final Intent data = new Intent();
	          data.putExtra("fbAccessToken", fbAccessToken);
	          data.putExtra("tabbieAccessToken", tabbieAccessToken);
	          data.putExtra("facebookName", facebookName);
	          data.putExtra("expires", expires);
	          this.setResult(RESULT_OK, data);
	          finish();
	        }
	      } else {
	    	Log.e(TAG, "Tabbie Log-in JSON does not have a token!");
	    	throw new RuntimeException();
	      }
	      break;
	    }
	    return true;
    }
}