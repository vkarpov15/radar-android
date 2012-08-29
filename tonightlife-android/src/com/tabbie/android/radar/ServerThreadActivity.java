package com.tabbie.android.radar;

/**
 * ServerThreadActivity.java
 * 
 * Created on: June 15, 2012
 * Author: vkarpov
 * 
 * Provides a general interface for Activities that want to use ServerThread,
 * basically taking care of some life cycle tasks related to the thread.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tabbie.android.radar.http.ServerRequest;
import com.tabbie.android.radar.http.ServerResponse;

public abstract class ServerThreadActivity extends Activity
	implements Handler.Callback {
	
	private ServerThread serverThread = null;
	private final Handler handler = new Handler(this);
	private static final String TAG = "ServerThreadActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // If this is a configuration change, get the thread back
    /*
    if (this.getLastNonConfigurationInstance() != null
        && this.getLastNonConfigurationInstance() instanceof ServerThread) {
    	Log.d(TAG, "Inside getLastNonConfigurationInstance block");
      this.serverThread = (ServerThread) this.getLastNonConfigurationInstance();
    }*/

    if (serverThread == null) {
      serverThread = new ServerThread("LoginThread", handler);
      serverThread.start();
    } else {
      serverThread.setUpstreamHandler(handler);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (serverThread != null) {
      serverThread.setUpstreamHandler(null);
    }
    this.serverThread.setInactive();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (serverThread != null) {
      serverThread.setUpstreamHandler(handler);
    }
    this.serverThread.setActive();
  }

  /** Send a message to a serverThread instance
   * 
   * @param req The request to be sent
   */
  
  public void sendServerRequest(final ServerRequest req) {
    this.serverThread.sendRequest(req);
  }

  @Override
  public boolean handleMessage(final Message msg) {
	  
	  if(msg.obj instanceof ServerResponse) {
	    final ServerResponse resp = (ServerResponse) msg.obj;
	    // Case statement always drops down to default
	    switch(resp.code)
	    {
	    case ServerResponse.NO_INTERNET:
			Toast.makeText(ServerThreadActivity.this,
					"No internet connection found!", Toast.LENGTH_LONG).show();
	    default:
	    	return handleServerResponse(resp);
	    }
	  } else {
		  return false;
	  }
  }

  /** Override this method to handle responses from the server
   * 
   * @param resp The message returned from the server
   * @return Whether the response was handled properly or not
   */
  
  protected abstract boolean handleServerResponse(final ServerResponse resp);
}
