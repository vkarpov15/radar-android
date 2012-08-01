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
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

public class RemoteDrawableController {
  private final LinkedHashMap<String, Drawable> myDrawables = new LinkedHashMap<String, Drawable>();
  private final ListView[] listViews;

  public RemoteDrawableController(final ListView... listViews)
  {
	  this.listViews = listViews;
  }

  public void preload(final URL u)
  {
	  if (myDrawables.containsKey(u.toString()))
		  return;
      else
      {
    	  new Thread(
    			  new Runnable() {
					
					@Override
					public void run() {
						try
						{
							Log.v("RemoteDrawableController", "Starting image retrieval thread");
							final Drawable d = Drawable.createFromStream(u.openStream(), "src");
							synchronized(RemoteDrawableController.this)
							{
								Log.v("RemoteDrawableController", "Lock on RDC, putting drawable");
								myDrawables.put(u.toString(), d);
							}
						}
						catch(final IOException e)
						{
							e.printStackTrace();
						}
						finally
						{
							onPreloadFinished();
						}
					}
				}).start();
      }
  }
  
  private void onPreloadFinished()
  {
	  Log.v("RemoteDrawableController", "Preload Finished");
  }
  
  protected boolean hasImage(final URL u)
  {
	  return myDrawables.containsKey(u.toString()) ? true : false;
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
