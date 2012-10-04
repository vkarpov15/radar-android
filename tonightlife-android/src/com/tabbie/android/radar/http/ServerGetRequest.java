package com.tabbie.android.radar.http;

import com.tabbie.android.radar.enums.MessageType;

/**
 * ServerGetRequest.java
 * 
 * Created on: June 19, 2012
 * Author: vkarpov
 * 
 * A specialized HTTP GET ServerRequest
 */

public class ServerGetRequest extends ServerRequest {
  public ServerGetRequest(String url, MessageType type) {
    super(url, "GET", type);
  }

  @Override
  public String getOutput() {
    return null;
  }

  @Override
  public boolean hasOutput() {
    return false;
  }

}
