package com.tabbie.android.radar;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
  public final String id;
  public final String name;
  public final String description;
  public final String venueName;
  public final String address;
  public final URL image;
  public final double lat;
  public final double lon;
  public final boolean featured;
  public final Date date;
  public final String time;

  public int radarCount;
  private boolean onRadar;

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
                String date,
                boolean onRadar) throws ParseException, IndexOutOfBoundsException {
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
    this.date = parseRFC3339Date(date);
    this.onRadar = onRadar;
    this.time = makeYourTime(this.date);
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
  
  protected String getAbbreviatedName(int maxLength)
  {
      if (name.length() > maxLength) {
          return name.substring(0, 34) + "...";
        }
      else return name;
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
    dest.writeString(date.toString());
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
      } catch(ParseException e) {
    	  e.printStackTrace();
    	  return null;
      } catch(IndexOutOfBoundsException e) {
    	  e.printStackTrace();
    	  return null;
      }
    }

    public Event[] newArray(int size) {
      return new Event[size];
    }
  };
  
  public static java.util.Date parseRFC3339Date(String datestring)
	      throws java.text.ParseException, IndexOutOfBoundsException {
	    Date d = new Date();

	    // if there is no time zone, we don't need to do any special parsing.
	    if (datestring.endsWith("Z")) {
	      try {
	        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");// spec
	                                                                              // for
	                                                                              // RFC3339
	        d = s.parse(datestring);
	      } catch (java.text.ParseException pe) {// try again with optional decimals
	        SimpleDateFormat s = new SimpleDateFormat(
	            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");// spec for RFC3339 (with
	                                               // fractional seconds)
	        s.setLenient(true);
	        d = s.parse(datestring);
	      }
	      return d;
	    }

	    // step one, split off the timezone.
	    String firstpart = datestring.substring(0, datestring.lastIndexOf('-'));
	    String secondpart = datestring.substring(datestring.lastIndexOf('-'));

	    // step two, remove the colon from the timezone offset
	    secondpart = secondpart.substring(0, secondpart.indexOf(':'))
	        + secondpart.substring(secondpart.indexOf(':') + 1);
	    datestring = firstpart + secondpart;
	    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");// spec
	                                                                        // for
	                                                                        // RFC3339
	    try {
	      d = s.parse(datestring);
	    } catch (java.text.ParseException pe) {// try again with optional decimals
	      s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");// spec for
	                                                                // RFC3339 (with
	                                                                // fractional
	                                                                // seconds)
	      s.setLenient(true);
	      d = s.parse(datestring);
	    }
	    return d;
	  }
  
  protected static String makeYourTime(final Date d) {
	    final String minutes = d.getMinutes() > 9 ? ":" + d.getMinutes() : ":0"
	        + d.getMinutes();
	    final int hours = d.getHours();
	    if (hours == 0)
	      return "12" + minutes + "am";
	    else if (hours > 0 && hours < 12)
	      return Integer.toString(hours) + minutes + "am";
	    else
	      return Integer.toString(hours - 12) + minutes + "pm";
	  }
}
