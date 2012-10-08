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

public abstract class GenericServerRequest {
	public static final String TAG = "GenericServerRequest";
	public final String url;
	public final String reqMethod;
	public final MessageType type;
	public final LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
	public Handler responseHandler = null;
	
	public GenericServerRequest(String reqMethod,
			MessageType type, String...extras) {

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < type.url.length; i++) {
			builder.append(type.url[i]);
			if(extras.length>i) {
				builder.append(extras[i]);
			}
		}
		this.url = new String(builder);
		this.reqMethod = reqMethod;
		this.type = type;
	}
	
	public abstract boolean hasOutput();
	
	public abstract String getOutput();
}