package com.tabbie.android.radar.http;

import com.tabbie.android.radar.MessageType;

/**
 * ServerDeleteRequest.java
 * 
 * Created on: July 30, 2012 Author: vkarpov
 * 
 * A specialized HTTP DELETE ServerRequest.
 */

public class ServerDeleteRequest extends ServerRequest {
  public ServerDeleteRequest(String url, MessageType type) {
    super(url, "DELETE", type);
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
