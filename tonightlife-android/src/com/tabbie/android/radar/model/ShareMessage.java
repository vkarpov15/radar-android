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
	
	// Immutable public constants
	public final String mMessage;
	public final String mEventId;
	public final String mUserFirstName;
	public final String mUserLastName;
	
	public ShareMessage(final String userFirstName, final String userLastName,
			final String message, final String eventId) {
		this.mUserFirstName = userFirstName;
		this.mUserLastName = userLastName;
		this.mMessage = message;
		this.mEventId = eventId;
	}
}
