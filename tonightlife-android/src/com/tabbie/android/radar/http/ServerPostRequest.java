package com.tabbie.android.radar.http;

/**
 * ServerPostRequest.java
 * 
 * Created on: June 19, 2012
 * Author: vkarpov
 * 
 * A specialized HTTP POST ServerRequest.
 */

import java.util.LinkedHashMap;

import com.tabbie.android.radar.MessageType;

public class ServerPostRequest extends ServerRequest {
  public final LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
  
  public ServerPostRequest(String url, MessageType type) {
    super(url, "POST", type);
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
