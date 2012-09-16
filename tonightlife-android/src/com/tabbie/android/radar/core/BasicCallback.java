package com.tabbie.android.radar.core;

/**
 *  BasicCallback.java
 * 
 *  Created on: September 15, 2012
 *      Author: Valeri Karpov
 * 
 *  High level interface for a callback.
 */

public interface BasicCallback<T> {
  public void onDone(T response);
  
  public void onFail(String reason);
}
