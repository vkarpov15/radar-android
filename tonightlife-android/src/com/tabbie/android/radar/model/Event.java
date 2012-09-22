package com.tabbie.android.radar.model;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.maps.GeoPoint;
import com.tabbie.android.radar.TonightlifeDatetime;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class Event implements Parcelable {
	public enum Energy {
		HIGH,
		MODERATE,
		LOW
	}
	public enum Price {
		FREE,
		CHEAP,
		EXPENSIVE
	}
  public final String id;
  public final String name;
  public final String description;
  public final String venue;
  public final String address;
  public final URL imageUrl;
  public final boolean isFeatured;
  public final TonightlifeDatetime time; 
  public int lineupCount;
  public boolean onLineup;
  public final GeoPoint location;
  public final Pair<String, String> rsvp;

  public Event(final String id,
		  	final String name,
		  	final String description,
		  	final String venueName,
		  	final String address,
		  	final URL image,
		  	final int latE6,
		  	final int lonE6,
		  	final int radarCount,
		  	final boolean featured,
		  	final String time,
		  	final boolean onRadar,
		  	final Pair<String, String> rsvp) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.venue = venueName;
    this.address = address;
    this.imageUrl = image;
    this.lineupCount = radarCount;
    this.isFeatured = featured;
    this.time = new TonightlifeDatetime(time);
    this.onLineup = onRadar;
    this.location = new GeoPoint(latE6, lonE6);
    this.rsvp = rsvp;
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
    dest.writeInt(location.getLatitudeE6());
    dest.writeInt(location.getLongitudeE6());
    dest.writeInt(lineupCount);
    dest.writeInt(isFeatured ? 1 : 0);
    dest.writeString(time.initializer);
    dest.writeInt(onLineup ? 1 : 0);
    dest.writeString(rsvp.first);
    dest.writeString(rsvp.second);
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
                          in.readInt(),
                          in.readInt(),
                          in.readInt(),
                          in.readInt() == 1,
                          in.readString(),
                          in.readInt() == 1,
                          new Pair<String, String> (in.readString(), in.readString()));
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
