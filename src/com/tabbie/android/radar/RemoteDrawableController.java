package com.tabbie.android.radar;

/*
 *  RemoteDrawableController.java
 *
 *  Created on: July 29, 2012
 *      Author: Valeri Karpov
 *      
 *  Data structure for asynchronously loading images from URLs with basic caching
 */

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class RemoteDrawableController {
  private final LinkedHashMap<String, Drawable> myDrawables = new LinkedHashMap<String, Drawable>();

  public RemoteDrawableController() {
  }

  public static abstract class OnImageLoadedCallback {
    public abstract void onDone(Drawable d);
  }

  public void drawImage(URL u, final OnImageLoadedCallback callback) {
    if (myDrawables.containsKey(u.toString())) {
      callback.onDone(myDrawables.get(u.toString()));
      return;
    }

    new AsyncTask<URL, Integer, Long>() {
      @Override
      protected Long doInBackground(URL... params) {
        for (URL url : params) {
          try {
            Drawable d = Drawable.createFromStream(url.openStream(), "src");
            callback.onDone(d);
            myDrawables.put(url.toString(), d);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return null;
      }
    }.execute(u);
  }
}
