package com.tabbie.android.radar.http;

import com.tabbie.android.radar.MessageType;

/**
 * ServerPutRequest.java
 * 
 * Created on: June 19, 2012
 * Author: vkarpov
 * 
 * A specialized HTTP PUT ServerRequest
 */

public class ServerPutRequest extends ServerRequest {
  private final String jsonContents;
  
  public ServerPutRequest(String url, MessageType type, String jsonContents) {
    super(url, "PUT", type);
    super.httpParams.put("Content-Type", "application/json");
    this.jsonContents = jsonContents;
  }

  @Override
  public boolean hasOutput() {
    return jsonContents.length() > 0;
  }

  @Override
  public String getOutput() {
    return jsonContents;
  }

}
