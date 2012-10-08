package com.tabbie.android.radar.http;

import com.tabbie.android.radar.enums.MessageType;

public class GenericServerPostRequest extends GenericServerRequest {

	public GenericServerPostRequest(MessageType type,
			String... extras) {
		super("POST", type, extras);
	}

	@Override
	public boolean hasOutput() {
    return params.size() > 0;
	}

	@Override
	public String getOutput() {
    // TODO: URI encode text
    if (0 == params.size()) {
      return null;
    }
    String st = "";
    for (String key : params.keySet()) {
      if (st.length() > 0) {
        st += "&";
      }
      st += key + "=" + params.get(key);
    }
    return st;
	}
}