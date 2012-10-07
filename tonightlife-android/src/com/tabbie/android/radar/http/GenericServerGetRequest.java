package com.tabbie.android.radar.http;

import com.tabbie.android.radar.enums.MessageType;

public class GenericServerGetRequest extends GenericServerRequest {

	public GenericServerGetRequest(MessageType type,
			String... extras) {
		super("GET", type, extras);
	}

	@Override
	public boolean hasOutput() {
		return false;
	}

	@Override
	public String getOutput() {
		return null;
	}
}
