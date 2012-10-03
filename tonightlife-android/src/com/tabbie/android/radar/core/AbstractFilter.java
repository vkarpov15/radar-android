package com.tabbie.android.radar.core;

/**
 *  Filter.java
 *
 *  Created on: July 23, 2012
 *      @author: Valeri Karpov
 *      
 *  A simple interface for creating filters for lists
 */

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFilter<T> {
  public abstract boolean apply(T o);
  
  public List<T> applyToList(List<T> list) {
    List<T> ret = new ArrayList<T>();
    for (T element : list) {
      if (apply(element)) {
        ret.add(element);
      }
    }
    return ret;
  }
}