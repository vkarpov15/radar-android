package com.tabbie.android.radar.model;

/**
 *  ShareMessage.java
 *
 *  Created on: September 7, 2012
 *      @author: Justin Knutson
 *      
 *  Simple object corresponding to a twitter-style
 *  SMS message that can be passed around and used
 *  to build message feeds.
 */

public class ShareMessage {
	public final static String TAG = "ShareMessage";
	public final String message;
	public final String userFirstName;
	public final String userLastName;
	
	public ShareMessage(final String firstName, final String lastName, final String message) {
		this.userFirstName = firstName;
		this.userLastName = lastName;
		this.message = message;
	}
}
