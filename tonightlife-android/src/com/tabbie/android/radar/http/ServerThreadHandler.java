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
		/*
		* Java occasionally includes HTTP headers in response. This prevents that from happening. Don't ask me why.
		* DO NOT FOR THE LOVE OF GOD EVER EVER DELETE THIS.
		*/
    System.setProperty("http.keepAlive", "false");
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if(msg.obj instanceof ServerRequest) {
			ServerRequest req = (ServerRequest) msg.obj;
			try {
				
				// Open URL Connection with the specified Request Method
				HttpURLConnection conn = (HttpURLConnection) new URL(req.mUrl).openConnection();
				conn.setRequestMethod(req.mReqMethod);
				
				// Pass parameters, if any
				for(String key : req.mParams.keySet()) {
				  conn.setRequestProperty(key, req.mParams.get(key));
				}
				
				// If we have parameters, we have output
				if (req.hasOutput()) {
				  conn.setDoOutput(true);
		      OutputStream stream = conn.getOutputStream();
		      stream.write(req.getOutput().getBytes());
		      stream.flush();
				} else {
			    conn.connect();
			  }
				
				// Check to see if there is a response error code
				int responseCode = conn.getResponseCode();
		    if (responseCode < 200 || responseCode >= 300) {
		    	Log.e(TAG, "Connection failed with error " + responseCode);
		      return;
		    }
		        
		    // Build a string from the output if we have no errors
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String y = "";
				while ((y = reader.readLine())!=null) {
					sb.append(y);
				}
				
				// Pass back any information we have
				if (req.mResponseHandler != null) {
					Message responseMessage = Message.obtain();
					responseMessage.obj = new ServerResponse(sb.toString(), req.mType);
					req.mResponseHandler.sendMessage(responseMessage);
				} else {
					Log.i(TAG, "No response handler available");
					return;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "Error: Message is not a ServerRequest");
			return;
		}
	}
}