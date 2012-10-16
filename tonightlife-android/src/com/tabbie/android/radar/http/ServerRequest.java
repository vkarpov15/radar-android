package com.tabbie.android.radar.http;

/**
 * GenericServerRequest.java
 * 
 * Created on: June 16, 2012
 * @author: Justin Knutson
 * 
 * A basic GENERIC interface for all the things we want from an HTTP request
 */

import java.util.LinkedHashMap;

import android.os.Handler;

import com.tabbie.android.radar.enums.MessageType;

public class ServerRequest {
	
	public static final String TAG = "GenericServerRequest";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String POST = "POST";
	
	public final String mUrl;
	public final String mReqMethod;
	public final MessageType mType;
	public final LinkedHashMap<String, String> mParams;
	public final Handler mResponseHandler;
	
	/*
	 * Overloaded Constructors, one for a handled response
	 * and one for an unchecked response
	 */
	public ServerRequest(MessageType type, String...extras) {

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < type.mUrl.length; i++) {
			builder.append(type.mUrl[i]);
			if(extras.length>i) {
				builder.append(extras[i]);
			}
		}
		
		this.mUrl = new String(builder);
		this.mReqMethod = type.mType;
		this.mType = type;
		this.mResponseHandler = null;
		this.mParams = new LinkedHashMap<String, String>();
	}
	
	public ServerRequest(MessageType type, Handler handler, String...extras) {

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < type.mUrl.length; i++) {
			builder.append(type.mUrl[i]);
			if(extras.length>i) {
				builder.append(extras[i]);
			}
		}
		this.mUrl = new String(builder);
		this.mReqMethod = type.mType;
		this.mType = type;
		this.mResponseHandler = handler;
		mParams = new LinkedHashMap<String, String>();
	}
	
	public boolean hasOutput() {
    return mParams.size() > 0;
	}
	
	public String getOutput() {
    if (0 == mParams.size()) {
      return null;
    }
    String st = "";
    for (String key : mParams.keySet()) {
      if (st.length() > 0) {
        st += "&";
      }
      st += key + "=" + mParams.get(key);
    }
    return st;
	}
}