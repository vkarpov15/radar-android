package com.tabbie.android.radar;

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
    this.jsonContents = jsonContents;
    this.httpParams.put("Content-Type", "application/json");
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
