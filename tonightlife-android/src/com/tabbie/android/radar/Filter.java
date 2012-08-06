package com.tabbie.android.radar;

/**
 *  Filter.java
 *
 *  Created on: July 23, 2012
 *      Author: Valeri Karpov
 *      
 *  A simple interface for creating filters for lists
 */

import java.util.ArrayList;
import java.util.List;

public abstract class Filter<T> {
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