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

public class RemoteDrawableController implements Handler.Callback {
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
  
  // TODO Use this for something
  /**
   * Asynchronously retrieve the image at URL u as a Bitmap
   * in a new Thread  
   * @param u - The URL of the Bitmap to be decoded
   * @param handler - An appropriate handler to deal with responses on the UI thread
   */
  private static void summonBitmap(final URL u, final Handler handler) {
	  new Thread(new Runnable() {
		
		@Override
		public void run() {
			final Message msg = Message.obtain();
			try {
				msg.obj = BitmapFactory.decodeStream(u.openStream());
			} catch(final IOException e) {
				msg.obj = null;
			} finally {
				handler.dispatchMessage(msg);
			}
		}
	}).start();
  }

  public interface PreLoadFinishedListener {
    public void onPreLoadFinished();
  }
  
@Override
/*
 * Native handler for dealing with incoming
 * Bitmaps that have successfully loaded from
 * summonBitmap or other appropriate method
 */
public boolean handleMessage(final Message msg) {
	if(msg.obj instanceof Bitmap) {
		// TODO When appropriate, switch this code over to Bitmaps instead of Drawables
		final Drawable d = new BitmapDrawable(context.getResources(), (Bitmap) msg.obj);
		// Making Bitmap object a member of Event
	}
	return false;
}
}