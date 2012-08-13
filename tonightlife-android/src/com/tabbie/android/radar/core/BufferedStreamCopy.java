package com.tabbie.android.radar.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  BufferedStreamCopy.java
 *
 *  Created on: August 12, 2012
 *      Author: Valeri Karpov
 *      
 *  Thread-safe way to copy from an input stream to an output stream. For details, see
 *  http://java.sun.com/docs/books/performance/1st_edition/html/JPIOPerformance.fm.html
 */

public class BufferedStreamCopy {
  private final int bufferSize;
  private final byte[] buffer;
  
  public BufferedStreamCopy(int bufferSize) {
    this.buffer = new byte[bufferSize];
    this.bufferSize = bufferSize;
  }
  
  public boolean copy(InputStream is, OutputStream os) {
    while (true) {
      try {
        synchronized(buffer) {
          int read = is.read(buffer);
          if (-1 == read) {
            break;
          }
          os.write(buffer, 0, read);
        }
      } catch(IOException e) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }
  
}
