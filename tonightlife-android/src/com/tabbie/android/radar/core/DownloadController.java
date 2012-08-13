package com.tabbie.android.radar.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 *  FileDownloadController.java
 *
 *  Created on: August 12, 2012
 *      Author: Valeri Karpov
 *      
 *  Download a file from a URL to local storage for later access
 */

public class DownloadController {
  private final BufferedStreamCopy bufferedStreamCopy;
  private final File cacheDirectory;
  
  private final Set<String> files = new LinkedHashSet<String>();
  
  public DownloadController(File cacheDirectory, BufferedStreamCopy bufferedStreamCopy) {
    this.bufferedStreamCopy = bufferedStreamCopy;
    this.cacheDirectory = cacheDirectory;
  }
  
  public void download(URL u) {
    String filename = URLEncoder.encode(u.toString());
    synchronized (files) {
      if (files.contains(filename)) {
        return;
      }
      files.add(filename);
    }
    File f = new File(cacheDirectory, filename);
    
    HttpURLConnection conn;
    try {
      f.createNewFile();
      conn = (HttpURLConnection) u.openConnection();
      conn.setConnectTimeout(30000);
      conn.setReadTimeout(30000);
      conn.setInstanceFollowRedirects(true);
      InputStream is = conn.getInputStream();
      OutputStream os = new FileOutputStream(f);
      bufferedStreamCopy.copy(is, os);
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException();
    } 
  }
  
  public boolean clear(URL u) {
    String filename = URLEncoder.encode(u.toString());
    synchronized (files) {
      if (!files.contains(filename)) {
        return false;
      }
    }
    File f = new File(cacheDirectory, filename);
    f.delete();
    return true;
  }
  
  public InputStream getReadStream(URL u) {
    String filename = URLEncoder.encode(u.toString());
    synchronized (files) {
      if (!files.contains(filename)) {
        return null;
      }
    }
    try {
      return new BufferedInputStream(new FileInputStream(new File(cacheDirectory, filename)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }
  
  public void clear() {
    File[] files = cacheDirectory.listFiles();
    if (null == files) {
      return;
    }
    
    for (File f : files) {
      f.delete();
    }
  }
}
