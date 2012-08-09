package com.tabbie.android.radar;

/**
 *  RemoteDrawableController.java
 *
 *  Created on: July 29, 2012
 *      Author: Valeri Karpov
 *      
 *  Data structure for loading and caching images
 */

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class RemoteDrawableController {
  private final LinkedHashMap<String, Drawable> myDrawables = new LinkedHashMap<String, Drawable>();
  private final Context context;

  public RemoteDrawableController(final Context context) {
    this.context = context;
  }

  public void preload(final URL u) {
    if (myDrawables.containsKey(u.toString())) {
      return;
    } else {
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Log.v("RemoteDrawableController", "Starting image retrieval thread");
            final Drawable d = Drawable.createFromStream(u.openStream(), "src");
            synchronized (RemoteDrawableController.this) {
              Log.v("RemoteDrawableController", "Lock on RDC, putting drawable");
              myDrawables.put(u.toString(), d);
            }
          } catch (final IOException e) {
            e.printStackTrace();
          } finally {
            // This is synchronized in the RadarActivity
            ((PreLoadFinishedListener) context).onPreLoadFinished(); 
          }
        }
      });
      // t.setPriority(Thread.MAX_PRIORITY);
      t.start();
    }
  }

  protected boolean hasImage(final URL u) {
    return myDrawables.containsKey(u.toString());
  }
  
  public Bitmap getAsParcelable(final URL u) {
    if (myDrawables.containsKey(u.toString())) {
      return ((BitmapDrawable) myDrawables.get(u.toString())).getBitmap();
    } else {
      Drawable d = null;
      try {
        d = Drawable.createFromStream(u.openStream(), "src");
          myDrawables.put(u.toString(), d);
      } catch (IOException e) {
        e.printStackTrace();;
      }
      return ((BitmapDrawable) d).getBitmap();
    }
  }

  public void drawImage(URL u, final ImageView view) {
    if (myDrawables.containsKey(u.toString())) {
      view.setImageDrawable(myDrawables.get(u.toString()));
    } else {
      Drawable d;
      try {
        d = Drawable.createFromStream(u.openStream(), "src");
        myDrawables.put(u.toString(), d);
        view.setImageDrawable(d);
        view.setTag(u);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public interface PreLoadFinishedListener {
    public void onPreLoadFinished();
  }
  

}