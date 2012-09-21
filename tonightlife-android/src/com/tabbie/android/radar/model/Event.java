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
	public static final int HIGH_ENERGY = 2, MODERATE_ENERGY = 1, LOW_ENERGY = 0;
	public static final int EXPENSIVE = 2, CHEAP = 1, FREE = 0;
	
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
  public final int energyLevel;
  public final int priceLevel;
  public final int minAge;
  public int lineupCount;
  public boolean onLineup = false;
  
  private Event(final Bundle strings,
  		final URL imageUrl,
  		final GeoPoint location,
  		final int lineupCount,
  		final boolean isFeatured,
  		final TLDatetime time,
  		final Pair<String, String> rsvp,
  		final int energyLevel,
  		final int priceLevel,
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
  	this.energyLevel = energyLevel;
  	this.priceLevel = priceLevel;
  	this.minAge = minimumAge;
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
		final int energyLevel = getEnergyLevel();
		final int priceLevel = getPriceLevel();
		
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
  
  private static int getEnergyLevel() {
  	return MODERATE_ENERGY;
  }
  
  private static int getPriceLevel() {
  	return CHEAP;
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
  	final Bundle strings = new Bundle();
  	strings.putString("id", id);
  	strings.putString("name", name);
  	strings.putString("description", description);
  	strings.putString("venue", venue);
  	strings.putString("address", address);
  	
  	dest.writeBundle(strings);
    dest.writeString(imageUrl.toString());
    dest.writeInt(location.getLatitudeE6());
    dest.writeInt(location.getLongitudeE6());
    dest.writeInt(lineupCount);
    dest.writeInt(isFeatured ? 1 : 0);
    dest.writeString(time.initializer);
    dest.writeInt(onLineup ? 1 : 0);
    dest.writeString(rsvp.first);
    dest.writeString(rsvp.second);
    dest.writeInt(energyLevel);
    dest.writeInt(priceLevel);
    dest.writeInt(minAge);
  }

  public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
    public Event createFromParcel(Parcel in) {
    	final Bundle strings = in.readBundle();
    	final URL imageUrl;
    	try {
    		imageUrl = new URL(in.readString());
    	} catch(MalformedURLException e) {
    		Log.e(TAG, "WHAT A TERRIBLE FAILURE: Unable to recreate event from parcel");
    		throw new RuntimeException();
    	}
    	return new Event(strings,
    			imageUrl,
    			new GeoPoint((int) (in.readInt()*1E6), (int) (in.readInt()*1E6)),
    			in.readInt(),
    			in.readInt() == 1,
    			new TLDatetime(in.readString()),
    			new Pair<String, String> (in.readString(), in.readString()),
    			in.readInt(),
    			in.readInt(),
    			in.readInt());
    }

    public Event[] newArray(int size) {
      return new Event[size];
    }
  };
}
