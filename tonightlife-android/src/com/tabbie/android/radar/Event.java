package com.tabbie.android.radar;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
  public final String id;
  public final String name;
  public final String description;
  public final String venue;
  public final String address;
  public final URL imageUrl;
  public final double lat;
  public final double lon;
  public final boolean isFeatured;
  public final TonightlifeDatetime time; 
  public int lineupCount;
  public boolean onLineup;

  public Event(final String id,
		  	final String name,
		  	final String description,
		  	final String venueName,
		  	final String address,
		  	final URL image,
		  	final double lat,
		  	final double lon,
		  	final int radarCount,
		  	final boolean featured,
		  	final String time,
		  	final boolean onRadar) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.venue = venueName;
    this.address = address;
    this.imageUrl = image;
    this.lat = lat;
    this.lon = lon;
    this.lineupCount = radarCount;
    this.isFeatured = featured;
    this.time = new TonightlifeDatetime(time);
    this.onLineup = onRadar;
  }
  
  public boolean isOnLineup() {
    return onLineup;
  }

  public void setOnLineup(final boolean onLineup) {
    this.onLineup = onLineup;
  }

  public int describeContents() {
    return 0;
  }
  
  public String toString() {
    return "'" + this.name + "' radarCount=" + lineupCount + " onRadar=" + onLineup;
  }

  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(imageUrl.toString());
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(description);
    dest.writeString(venue);
    dest.writeString(address);
    dest.writeDouble(lat);
    dest.writeDouble(lon);
    dest.writeInt(lineupCount);
    dest.writeInt(isFeatured ? 1 : 0);
    dest.writeString(time.initializer);
    dest.writeInt(onLineup ? 1 : 0);
  }

  public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
    public Event createFromParcel(Parcel in) {
      final String url = in.readString();
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
      } catch (final MalformedURLException e) {
        e.printStackTrace();
        return null;
      }
    }

    public Event[] newArray(int size) {
      return new Event[size];
    }
  };
}
