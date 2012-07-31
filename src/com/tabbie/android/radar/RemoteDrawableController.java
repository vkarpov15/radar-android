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

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class RemoteDrawableController {
  private final LinkedHashMap<String, Drawable> myDrawables = new LinkedHashMap<String, Drawable>();

  public RemoteDrawableController() {
  }

  public void preload(URL u) {
    if (myDrawables.containsKey(u.toString())) {
      return;
    } else {
      Drawable d;
      try {
        d = Drawable.createFromStream(u.openStream(), "src");
        myDrawables.put(u.toString(), d);
      } catch (IOException e) {
        e.printStackTrace();
      }
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

}
