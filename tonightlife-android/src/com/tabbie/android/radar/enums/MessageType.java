package com.tabbie.android.radar.enums;

/**
 *  MessageType.java
 * 
 *  Created on: June 21, 2012
 *      Author: Valeri Karpov
 * 
 *  An enum of all of the different messages I can send to the server
 */

public enum MessageType {
  FACEBOOK_LOGIN("https://graph.facebook.com/me/?access_token="),
  TABBIE_LOGIN("http://23.21.40.96/mobile/v1/auth.json"),
  REGISTER_GCM("http://23.21.40.96/mobile/test/gcm_key/", ".json?auth_token="),
  LOAD_EVENTS("http://23.21.40.96/mobile/v1/all.json?auth_token="),
  LOAD_FRIENDS(),
  ADD_TO_RADAR("http://23.21.40.96/mobile/v1/radar/", ".json"),
  REMOVE_FROM_RADAR("http://23.21.40.96/mobile/v1/radar/", ".json?auth_token=");
  
  public final String[] url;
  private MessageType(String... params) {
  	this.url = params;
  }
}