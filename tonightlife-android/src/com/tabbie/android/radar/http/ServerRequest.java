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

import com.tabbie.android.radar.MessageType;

public abstract class ServerRequest {
	public final String url;
	public final String reqMethod;
	public final MessageType type;
	
	public final LinkedHashMap<String, String> httpParams = new LinkedHashMap<String, String>();
	
	public ServerRequest(String url, String reqMethod, MessageType type) {
		this.url = url;
		this.reqMethod = reqMethod;
		this.type = type;
	}
	
	public abstract boolean hasOutput();
	
	public abstract String getOutput();
}
