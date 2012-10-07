package com.tabbie.android.radar.http;

import com.tabbie.android.radar.enums.MessageType;

public class GenericServerPutRequest extends GenericServerRequest {

	public GenericServerPutRequest(MessageType type,
			String... extras) {
		super("PUT", type, extras);
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
