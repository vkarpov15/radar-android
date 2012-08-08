package com.tabbie.android.radar;

/**
 * ServerThreadActivity.java
 * 
 * Created on: June 15, 2012
 * Author: vkarpov
 * 
 * Provides a general interface for Activities that want to use ServerThread,
 * basically taking care of some life cycle tasks related to the thread. Also
 * provides a decent way for dealing with ProgressDialog during configuration
 * changes, and handles Zubhium stuff.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tabbie.android.radar.http.ServerRequest;
import com.tabbie.android.radar.http.ServerResponse;
import com.zubhium.ZubhiumSDK;

public abstract class ServerThreadActivity extends Activity {
  protected boolean showing = false;
  protected ServerThread serverThread = null;

  protected ZubhiumSDK sdk;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // sdk = ZubhiumSDK.getZubhiumSDKInstance(getApplicationContext(),
    // "cf36201cec04f886a90fd08eb44b00");

    // If this is a configuration change, get the thread back
    if (this.getLastNonConfigurationInstance() != null
        && this.getLastNonConfigurationInstance() instanceof ServerThread) {
      this.serverThread = (ServerThread) this.getLastNonConfigurationInstance();
    }

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

  // Override this method to handle responses from the server
  protected abstract boolean handleServerResponse(ServerResponse resp);

  public void sendServerRequest(ServerRequest req) {
    showing = true;
    this.serverThread.sendRequest(req);
  }

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      ServerResponse resp = (ServerResponse) msg.obj;
      if (resp.code == ServerThread.NO_INTERNET) {
        ServerThreadActivity.this.runOnUiThread(new Runnable() {
          public void run() {
            Toast.makeText(ServerThreadActivity.this,
                "No internet connection found!", 5000).show();
          }
        });
      }
      handleServerResponse(resp);
    }
  };
}
