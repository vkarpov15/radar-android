package com.tabbie.android.radar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  RadarCommonController.java
 *
 *  Created on: August 12, 2012
 *      Author: Valeri Karpov
 *      
 *  Thread-safe way to copy from an input stream to an output stream
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
