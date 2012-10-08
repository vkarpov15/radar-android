package com.tabbie.android.radar.http;

/**
 *  ServerThreadHandler.java
 * 
 *  Created on: September 4, 2012
 *  @Author: Justin Knutson
 *  
 *  Abstract handler for communicating with
 *  the Tabbie server
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class ServerThreadHandler extends Handler {
	public static final String TAG = "ServerThreadHandler";
	
	public ServerThreadHandler(final Looper looper) {
		super(looper);
		// Java occasionally includes HTTP headers in response. This prevents that from happening. Don't ask me why.
		// DO NOT FOR THE LOVE OF GOD EVER EVER DELETE THIS.
    System.setProperty("http.keepAlive", "false");
	}
	
	@Override
	public void handleMessage(final Message msg) {
		super.handleMessage(msg);
		if(!(msg.obj instanceof GenericServerRequest)) {
			Log.e(TAG, "Error: Message is not a ServerRequest");
			return;
		}
		final GenericServerRequest req = (GenericServerRequest) msg.obj;
		try {
			String TAG = "Generic side of ServerThreadHandler";
			Log.d(TAG, "Made it into the try statement");
			Log.d(TAG, "URL: " + req.url);
			final HttpURLConnection conn = (HttpURLConnection) new URL(req.url).openConnection();
			conn.setRequestMethod(req.reqMethod);
			for (final String key : req.params.keySet()) {
				Log.d(TAG, "Putting httpParams");
			  conn.setRequestProperty(key, req.params.get(key));
			}
			Log.d(TAG, conn.toString()); // TODO remove
			
			if (req.hasOutput()) {
			  conn.setDoOutput(true);
	      OutputStream stream = conn.getOutputStream();
	      stream.write(req.getOutput().getBytes());
	      stream.flush();
			} else {
		    conn.connect();
		  }
			
	    if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
	      // TODO Connection failed
	    }
	        
			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final StringBuilder sb = new StringBuilder();
			
			String y = "";
			while ((y = reader.readLine())!=null) {
				sb.append(y);
			}
			
			if (req.responseHandler != null) {
				final Message responseMessage = Message.obtain();
				responseMessage.obj = new ServerResponse(0, sb.toString(), req.type);
				req.responseHandler.sendMessage(responseMessage);
			} else {
				Log.i(TAG, "No response handler available");
				return;
			}
		} catch (final MalformedURLException e) {
			e.printStackTrace();
			// TODO Handleme
		} catch (final IOException e) {
			e.printStackTrace();
			// TODO Handleme
		}
	}
}