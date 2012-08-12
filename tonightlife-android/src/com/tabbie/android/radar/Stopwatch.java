package com.tabbie.android.radar;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Stopwatch.java
 * 
 *  Created on: August 11, 2012
 *  Author: Valeri Karpov
 * 
 *  Class for gathering timing statistics on multi-threaded code.
 */

public class Stopwatch {
  private final Map<String, Long> totals = new LinkedHashMap<String, Long>();
  private final Map<String, Long> counts = new LinkedHashMap<String, Long>();

  private final Map<Long, Long> prev = new LinkedHashMap<Long, Long>();
  private final Map<Long, String> prevTag = new LinkedHashMap<Long, String>();

  private boolean enabled = false;
  private long id = 0;

  public Stopwatch() {
  }

  public void enable() {
    enabled = true;
  }

  public void disable() {
    enabled = false;
  }

  public void init(String tag) {
    totals.put(tag, (long) 0);
    counts.put(tag, (long) 0);
  }

  /** Returns an identifier to pass as second parameter to finishProfiling,
   *  in case you have multiple threads profiling with the same tag */
  public long startProfiling(String tag) {
    long ret = 0;
    synchronized (Stopwatch.this) {
      ret = id++;
      prev.put(ret, System.currentTimeMillis());
      prevTag.put(ret, tag);
    }
    return ret;
  }
  
  public long finishProfiling(String tag, long prevIdentifier) {
    long deltaTime = 0;
    synchronized (Stopwatch.this) {
      long startTime = prev.get(prevIdentifier);
      deltaTime = System.currentTimeMillis() - startTime;
      prevTag.remove(prevIdentifier);
      prev.remove(prevIdentifier);
      
      totals.put(tag, totals.get(tag) + deltaTime);
      counts.put(tag, counts.get(tag) + 1);
    }
    return deltaTime;
  }

}
