package com.tabbie.android.radar.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.google.android.maps.GeoPoint;
import com.tabbie.android.radar.R;
import com.tabbie.android.radar.TLDatetime;

public class Event implements Parcelable {
	public static final String TAG = "Event";
	
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
  public final TLDatetime time;
  public final GeoPoint location;
  public final Pair<String, String> rsvp; 
  public int lineupCount;
  public boolean onLineup = false;
  
  private Event(final Bundle strings,
  		final URL imageUrl,
  		final GeoPoint location,
  		final int lineupCount,
  		final boolean isFeatured,
  		final TLDatetime time,
  		final Pair<String, String> rsvp,
  		final Energy energyLevel,
  		final Price priceLevel,
  		final int minimumAge) {
  	this.id = strings.getString("id");
  	this.name = strings.getString("name");
  	this.description = strings.getString("description");
  	this.venue = strings.getString("venue");
  	this.address = strings.getString("address");
  	this.imageUrl = imageUrl;
  	this.location = location;
  	this.lineupCount = lineupCount;
  	this.isFeatured = isFeatured;
  	this.time = time;
  	this.rsvp = rsvp;
  }

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
    this.time = new TLDatetime(time);
    this.onLineup = onRadar;
    this.location = new GeoPoint(latE6, lonE6);
    this.rsvp = rsvp;
  }
  
  public static Event buildFromJson(Context context, JSONObject eventJson) {
		
  	/* Try to coerce user lineup count from JSON Object
  	* and resort to default value (0) on failure
  	*/
  	int lineupCount = 0;
  	try {
  		final String lineupCountStr = eventJson.getString("user_count");
			if (null != lineupCountStr && 0 != lineupCountStr.compareTo("null")) {
				lineupCount = Integer.parseInt(lineupCountStr);
			}
  	} catch(JSONException e) {
  		Log.e(TAG, "Non-fatal Error: Could not coerce user_count");
  	}
  	
  	/*
  	 * Try to coerce the RSVP field and pass
  	 * null if a non-critical error occurs
  	 */
  	Pair<String, String> rsvp;
  	try {
  		rsvp = getRSVP(eventJson.getJSONObject("rsvp"));
  	} catch(JSONException e) {
  		Log.e(TAG, "Non-fatal Error: RSVP could not be coerced");
  		rsvp = null;
  	}
  	
  	// TODO Legitimize these fields
		final Energy energyLevel = getEnergyLevel();
		final Price priceLevel = getPriceLevel();
		
		/*
		 * Attempt to retrieve critical event components
		 * Fail slightly less gracefully if the event
		 * cannot be built from these components
		 */
		final Bundle strings = new Bundle();
		final URL imageUrl;
		try {
			strings.putString("id", eventJson.getString("id"));
			strings.putString("name", eventJson.getString("name"));
			strings.putString("description", eventJson.getString("description"));
			strings.putString("venue", eventJson.getString("location"));
			strings.putString("address", eventJson.getString("street_address"));
			imageUrl = new URL(context.getString(R.string.tabbie_server) + eventJson.getString("image_url"));
			
		} catch(JSONException e) {
			Log.e(TAG, "Critical error instantiating event");
			Log.v("Failed JSON Parse", eventJson.toString());
			return null;
		} catch(MalformedURLException e) {
			Log.e(TAG, "Critical error: Could not parse event image URL");
			return null;
		}
		
		/*
		 * Build a GeoPoint for this event. This
		 * could return null if the long/lat is
		 * unknown but the event is otherwise
		 * well-formed
		 */
		GeoPoint location;
		try {
			location = buildGeoPoint(eventJson);
		} catch(JSONException e) {
			Log.e(TAG, "Non-fatal error: Unable to build GeoPoint");
			location = null;
		}
		
		/*
		 * Attempt to coerce the boolean value
		 * whether this event is featured or not
		 */
		boolean featured;
		try {
			featured = eventJson.getBoolean("featured");
		} catch(JSONException e) {
			Log.e(TAG, "Non-fatal error: Unable to coerce boolean featured");
			featured = false;
		}
		
		/*
		 * Attempt to coerce a TLDatetime from
		 * the String time value in the event
		 * JSON Object. This is a critical value
		 */
		final TLDatetime time;
		try {
			time = new TLDatetime(eventJson.getString("start_time"));
		} catch(JSONException e) {
			Log.e(TAG, "Critical error: Could not parse TLDatetime");
			return null;
		}
		
		// TODO Pass actual minimum age
		return new Event(strings, imageUrl, location, lineupCount,
				featured, time, rsvp, energyLevel, priceLevel, 0);
  }
  
  private static Pair<String, String> getRSVP(JSONObject j) throws JSONException {
		if (j.has("url")) {
			return new Pair<String, String>("url", j.getString("url"));
		} else if (j.has("email")) {
			return new Pair<String, String>("email", j.getString("email"));
		} else {
			return new Pair<String, String>("", "");
		}
  }
  
  private static Energy getEnergyLevel() {
  	return Energy.MODERATE;
  }
  
  private static Price getPriceLevel() {
  	return Price.CHEAP;
  }

  private static GeoPoint buildGeoPoint(final JSONObject j) throws JSONException {
  	final int lat = (int) (j.getDouble("latitude")*1E6);
  	final int lon = (int) (j.getDouble("longitude")*1E6);
  	return new GeoPoint(lat, lon);
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
