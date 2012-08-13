package com.tabbie.android.radar;

/**
 *  MemoryCache.java
 *
 *  Created on: August 9, 2012
 *      Author: Fedor Vlasov, edited Justin Knutson
 *      Source: https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/MemoryCache.java
 *      
 *  Data structure for loading and caching images
 */

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {
  private static final String TAG = "MemoryCache";
    
  private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
  private long size = 0;
  private long limit = 1000000;

  public MemoryCache() {
    //use 25% of available heap size
    setLimit(Runtime.getRuntime().maxMemory() / 4);
  }
    
  public void setLimit(long limit) {
    this.limit = limit;
    Log.i(TAG, "MemoryCache will use up to " + (limit/1024./1024.) + "MB");
  }

  /**
   * Restore a cached Bitmap
   * @param id - The String identifier of the Bitmap to be restored
   * @return The cached Bitmap or null if not present or an error is thrown
   */
  public Bitmap get(String id) {
    try {
      if(!cache.containsKey(id)) {
        return null;
      }
      //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
      return cache.get(id);
    } catch(NullPointerException ex) {
      Log.e(TAG, "Caught an error, returning null");
      return null;
    }
  }

  /**
   * Add a new Bitmap to the cache or replace an old one
   * @param id - The String key associated with this entry
   * @param bitmap - The Bitmap to cache
   */
  public void put(String id, Bitmap bitmap) {
    try {
      if (cache.containsKey(id)) {
        size -= getSizeInBytes(cache.get(id));
      }
      cache.put(id, bitmap);
      size += getSizeInBytes(bitmap);
      checkSize();
    } catch(Throwable th) {
      th.printStackTrace();
    }
  }
    
  /**
   * Helper method to maintain a cache size less than limit
   * by removing least used old cached Bitmaps
   */
  private void checkSize() {
    Log.i(TAG, "cache size="+size+" length="+cache.size());
    if (size > limit) {
      Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator(); //least recently accessed item will be the first one iterated  
      while(iter.hasNext()) {
        Entry<String, Bitmap> entry = iter.next();
        size -= getSizeInBytes(entry.getValue());
        iter.remove();
        if(size <= limit) {
          break;
        }
      }
      Log.i(TAG, "Clean cache. New size "+cache.size());
    }
  }

  /**
   * Clear the cache
   */
  public void clear() {
    cache.clear();
  }

  /**
   * Method to calculate the size of a given Bitmap
   * @param bitmap - the Bitmap to size
   * @return The number of bytes in the Bitmap
   */
  long getSizeInBytes(Bitmap bitmap) {
    if (null == bitmap) {
      return 0;
    }
    return bitmap.getRowBytes() * bitmap.getHeight();
  }
}