package com.tabbie.android.radar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable, Handler.Callback {
  public final String id;
  public final String name;
  public final String description;
  public final String venueName;
  public final String address;
  public final URL image;
  public final double lat;
  public final double lon;
  public final boolean featured;
  public final TonightlifeDatetime time;

  public int radarCount;
  private boolean onRadar;
  private Bitmap eventImage;

  public Event( String id,
                String name,
                String description,
                String venueName,
                String address,
                URL image,
                double lat,
                double lon,
                int radarCount,
                boolean featured,
                String time,
                boolean onRadar) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.venueName = venueName;
    this.address = address;
    this.image = image;
    this.lat = lat;
    this.lon = lon;
    this.radarCount = radarCount;
    this.featured = featured;
    this.time = new TonightlifeDatetime(time);
    this.onRadar = onRadar;
  }

  public boolean isOnRadar() {
    return onRadar;
  }

  public void setOnRadar(boolean onRadar) {
    this.onRadar = onRadar;
  }

  public int describeContents() {
    return 0;
  }
  
  public String toString() {
    return "'" + this.name + "' radarCount=" + radarCount + " onRadar=" + onRadar;
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

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(image.toString());
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(description);
    dest.writeString(venueName);
    dest.writeString(address);
    dest.writeDouble(lat);
    dest.writeDouble(lon);
    dest.writeInt(radarCount);
    dest.writeInt(featured ? 1 : 0);
    dest.writeString(time.initializer);
    dest.writeInt(onRadar ? 1 : 0);
  }

  public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
    public Event createFromParcel(Parcel in) {
      String url = in.readString();
      try {
        return new Event( in.readString(),
                          in.readString(),
                          in.readString(),
                          in.readString(),
                          in.readString(),
                          new URL(url),
                          in.readDouble(),
                          in.readDouble(),
                          in.readInt(),
                          in.readInt() == 1,
                          in.readString(),
                          in.readInt() == 1);
      } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
      }
    }

    public Event[] newArray(int size) {
      return new Event[size];
    }
  };

  @Override
  public boolean handleMessage(final Message msg) {
	  // TODO Deal with Bitmap here
  	return false;
  }
}
