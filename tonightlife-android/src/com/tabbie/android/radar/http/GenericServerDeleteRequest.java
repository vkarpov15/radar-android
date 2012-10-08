package com.tabbie.android.radar.http;

import com.tabbie.android.radar.enums.MessageType;

public class GenericServerDeleteRequest extends GenericServerRequest {

	public GenericServerDeleteRequest(MessageType type,
			String[] extras) {
		super("DELETE", type, extras);
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
