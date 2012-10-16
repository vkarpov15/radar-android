package com.tabbie.android.radar.enums;

import com.tabbie.android.radar.http.ServerRequest;

/**
 * Revised: October 16, 2012
 * @author Justin Knutson
 * 
 * Significant changes to the basic structure of this enum,
 * including String variables for the different server requests
 * and urls
 * 
 *  MessageType.java
 * 
 *  Created on: June 21, 2012
 *      Author: Valeri Karpov
 * 
 *  An enum of all of the different messages I can send to the server
 */

public enum MessageType {
	
	// Post requests
  TABBIE_LOGIN(ServerRequest.POST, 	"http://23.21.40.96/mobile/v1/auth.json"),
  LOAD_FRIENDS(ServerRequest.POST, 	"http://23.21.40.96/mobile/test/friends.json"),
  POST_MESSAGE(ServerRequest.POST, 	"http://23.21.40.96/mobile/test/gcm.json"),
  ADD_TO_LINEUP(ServerRequest.POST, "http://23.21.40.96/mobile/v1/radar/", ".json"),
  
  // Get requests
  LOAD_EVENTS(ServerRequest.GET, 		"http://23.21.40.96/mobile/v1/all_authed.json?auth_token="),
  FACEBOOK_LOGIN(ServerRequest.GET, "https://graph.facebook.com/me/?access_token="),
  
  // Put requests
  REGISTER_GCM(ServerRequest.PUT, 	"http://23.21.40.96/mobile/test/gcm_key/", ".json?auth_token="),
  
  // Delete requests
  REMOVE_FROM_LINEUP(ServerRequest.DELETE,
  																	"http://23.21.40.96/mobile/v1/radar/", ".json?auth_token=");
  
  public final String[] mUrl;
  public final String mType;
  
  private MessageType(String type, String... params) {
  	this.mType = type;
  	this.mUrl = params;
  }
}