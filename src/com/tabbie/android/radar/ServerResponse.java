package com.tabbie.android.radar;

/**
 * ServerResponse.java
 * 
 * Created on: June 16, 2012
 * Author: vkarpov
 * 
 * Interface for what we care about from server responses.
 */

import org.json.JSONException;
import org.json.JSONObject;

public class ServerResponse {
	public final int code;
	public final String content;
	public final MessageType responseTo;
	
	public ServerResponse(int code, String content, MessageType responseTo) {
		this.code = code;
		this.content = content;
		this.responseTo = responseTo;
	}
	
	public JSONObject parseJsonContent() {
		try {
			return new JSONObject(this.content);
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
}
