package com.tabbie.android.radar.core;

/**
 *  DebugUtils.java
 * 
 *  Created on: September 15, 2012
 *      @author: Justin Knutson
 * 
 *  Simple tools and constants for debugging
 */

import java.util.List;

import android.os.Bundle;
import android.util.Pair;

public final class DebugUtils {
	// Set this to true to enable conditional error logs
	public static final boolean DEBUG = false;
	
  public static final boolean checkBundle(List<Pair<String, Class<?> > > expected, Bundle b) {
    if (null == b) {
      return false;
    }
    for (Pair<String, Class<?> > p : expected) {
      if (!b.containsKey(p.first)) {
        return false;
      }
      // TODO: Actual type checks
    }
    return true;
  }
}