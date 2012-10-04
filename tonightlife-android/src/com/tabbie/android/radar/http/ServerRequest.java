package com.tabbie.android.radar.http;

/**
 * ServerRequest.java
 * 
 * Created on: June 16, 2012
 * Author: vkarpov
 * 
 * A basic interface for all the things we want from an HTTP request
 */

import java.util.LinkedHashMap;

import android.os.Handler;

import com.tabbie.android.radar.enums.MessageType;

public abstract class ServerRequest {
	public final String url;
	public final String reqMethod;
	public final MessageType type;
	public final LinkedHashMap<String, String> httpParams = new LinkedHashMap<String, String>();
	public Handler responseHandler = null;
	
	public ServerRequest(final String url,
			final String reqMethod,
			final MessageType type) {
		
		this.url = url;
		this.reqMethod = reqMethod;
		this.type = type;
	}
	
	public abstract boolean hasOutput();
	
	public abstract String getOutput();
}
