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

import com.tabbie.android.radar.MessageType;

public abstract class ServerRequest {
	private final String url;
	private final String reqMethod;
	private final MessageType type;
	private final LinkedHashMap<String, String> httpParams = new LinkedHashMap<String, String>();
	
	public Handler responseHandler = null;
	
	public ServerRequest(final String url,
			final String reqMethod,
			final MessageType type) {
		
		this.url = url;
		this.reqMethod = reqMethod;
		this.type = type;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getReqMethod() {
		return reqMethod;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public LinkedHashMap<String, String> getHttpParams() {
		return httpParams;
	}
	
 	protected void put(final String key, final String value) {
		this.httpParams.put(key, value);
	}
	
	public abstract boolean hasOutput();
	
	public abstract String getOutput();
}
