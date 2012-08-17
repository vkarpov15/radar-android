package com.tabbie.android.radar.core;

import java.util.List;

import android.os.Bundle;
import android.util.Pair;

/**
 *  BundleChecker.java
 *
 *  Created on: August 16, 2012
 *      Author: Valeri Karpov
 *      
 *  For ease of readability as well as catching annoying bugs before
 *  they waste our time, a simple checker to make sure bundle exists
 *  and has what we want.
 *  
 */

public class BundleChecker {
  private final Bundle b;
  
  public BundleChecker(final Bundle b) {
    this.b = b;
  }
  
  public boolean check(List<Pair<String, Class<?> > > expected) {
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
