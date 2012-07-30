package com.tabbie.android.radar;

/**
 * ServerDeleteRequest.java
 * 
 * Created on: July 30, 2012
 * Author: vkarpov
 * 
 * A specialized HTTP DELETE ServerRequest.
 */

import java.util.LinkedHashMap;

public class ServerDeleteRequest extends ServerRequest {
  public final LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

  public ServerDeleteRequest(String url, MessageType type) {
    super(url, "DELETE", type);
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
