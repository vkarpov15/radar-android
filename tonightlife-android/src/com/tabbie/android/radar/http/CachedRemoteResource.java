package com.tabbie.android.radar.http;

import android.content.SharedPreferences;

/**
 *  CachedRemoteResource.java
 * 
 *  Created on: October 21, 2012
 *      Author: Valeri Karpov
 * 
 *  Abstract class for storing data which expires in shared prefs
 * 
 */

public abstract class CachedRemoteResource {
  protected final SharedPreferences prefs;
  
  private Long expires;
  
  public CachedRemoteResource(String expiresStr, SharedPreferences prefs) {
    this.prefs = prefs;
    
    this.expires = prefs.getLong(expiresStr, 0);
  }
  
  public boolean isValid() {
    return System.currentTimeMillis() < expires;
  }
}
