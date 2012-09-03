package com.tabbie.android.radar;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable, Handler.Callback {
  private final String tag;
  private final String name;
  private final String description;
  private final String venue;
  private final String address;
  private final URL imageUrl;
  private final double lat;
  private final double lon;
  private final boolean isFeatured;
  private final TonightlifeDatetime time; 

  public int radarCount;
  private boolean onRadar;

  public Event(String id,
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
    this.tag = id;
    this.name = name;
    this.description = description;
    this.venue = venueName;
    this.address = address;
    this.imageUrl = image;
    this.lat = lat;
    this.lon = lon;
    this.radarCount = radarCount;
    this.isFeatured = featured;
    this.time = new TonightlifeDatetime(time);
    this.onRadar = onRadar;
  }
  
  public String getTag() {
	  return tag;
  }
  
  public URL getUrl() {
	  return imageUrl;
  }
  
  public String getName() {
	  return name;
  }

  public TonightlifeDatetime getTime() {
	  return time;
  }
  
  public String getVenueName() {
	  return venue;
  }
  
  public String getAddress() {
	  return address;
  }
  
  public String getDescription() {
	  return description;
  }
  
  public boolean isFeatured() {
	  return isFeatured;
  }
  
  public double getLatitude() {
	  return lat;
  }
  
  public double getLongitude() {
	  return lon;
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

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(imageUrl.toString());
    dest.writeString(tag);
    dest.writeString(name);
    dest.writeString(description);
    dest.writeString(venue);
    dest.writeString(address);
    dest.writeDouble(lat);
    dest.writeDouble(lon);
    dest.writeInt(radarCount);
    dest.writeInt(isFeatured ? 1 : 0);
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
