package com.tabbie.android.radar.model;

/**
 * Ass ass ass ass
 * 
 * ASS ASS ASS ASS
 * 
 * Stop.
 * 
 * Now make that motherfucker Hammertime
 */
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
	
	// Static field constants
	public static final String TAG = "Event";
	public static final int HIGH_ENERGY = 2, MODERATE_ENERGY = 1, LOW_ENERGY = 0;
	public static final int EXPENSIVE = 2, CHEAP = 1, FREE = 0;
	
	// Public-facing final constants
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
  
  // Mutable lineup variables
  public int lineupCount;
  public boolean onLineup = false;
  
  // Parcelable data bundle
  private final Bundle data;
  
  private Event(final Bundle data) {
  	this.data = data;
  	this.id = data.getString("id");
  	this.name = data.getString("name");
  	this.description = data.getString("description");
  	this.venue = data.getString("venue");
  	this.address = data.getString("address");
  	URL testUrl = null;
  	try {
  		testUrl = new URL(data.getString("imageUrl"));
  	} catch(MalformedURLException e) {
  		Log.e(TAG, "Non-critical error: Unable to construct event image URL");
  	} finally {
  		this.imageUrl = testUrl;
  	}
  	this.location = new GeoPoint(data.getInt("lat"), data.getInt("lon"));
  	this.lineupCount = data.getInt("lineupCount");
  	this.isFeatured = data.getBoolean("isFeatured");
  	this.time = new TLDatetime(data.getString("time"));
  	this.rsvp = new Pair<String, String>(data.getString("first"), data.getString("second"));
  	this.energyLevel = data.getInt("energyLevel");
  	this.priceLevel = data.getInt("priceLevel");
  	this.minAge = data.getInt("minAge");
  	if(data.containsKey("onLineup")) {
  		onLineup = data.getBoolean("onLineup");
  	}
  }
  
  /**
   * Builder method for Event. Events can
   * be instantiated two ways - the first
   * is to pass JSON through buildFromJson
   * and capture the result. The other is
   * through parceling the event.
   * 
   * @param context Necessary to
   * obtain String resources
   * 
   * @param eventJson The JSON to be parsed
   * 
   * @return A new Event object or NULL if
   * a critical parse failure was encountered
   */
  public static Event buildFromJson(Context context, JSONObject eventJson) {
		final Bundle data = new Bundle();
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
  	data.putInt("lineupCount", lineupCount);
  	
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
  	data.putString("first", rsvp.first);
  	data.putString("second", rsvp.second);
  	
  	// TODO Legitimize these fields
		data.putInt("energyLevel", getEnergyLevel());
		data.putInt("priceLevel", getPriceLevel());
		
		/*
		 * Attempt to retrieve critical event components
		 * Fail slightly less gracefully if the event
		 * cannot be built from these components
		 */
		try {
			data.putString("id", eventJson.getString("id"));
			data.putString("name", eventJson.getString("name"));
			data.putString("description", eventJson.getString("description"));
			data.putString("venue", eventJson.getString("location"));
			data.putString("address", eventJson.getString("street_address"));
			data.putString("imageUrl", context.getString(R.string.tabbie_server) + eventJson.getString("image_url"));
			
		} catch(JSONException e) {
			Log.e(TAG, "Critical error instantiating event");
			Log.v("Failed JSON Parse", eventJson.toString());
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
		data.putInt("lat", location.getLatitudeE6());
		data.putInt("lon", location.getLongitudeE6());
		
		/*
		 * Attempt to coerce the boolean value
		 * whether this event is featured or not
		 */
		boolean isFeatured;
		try {
			isFeatured = eventJson.getBoolean("featured");
		} catch(JSONException e) {
			Log.e(TAG, "Non-fatal error: Unable to coerce boolean featured");
			isFeatured = false;
		}
		data.putBoolean("isFeatured", isFeatured);
		
		/*
		 * Attempt to coerce a TLDatetime from
		 * the String time value in the event
		 * JSON Object. This is a critical value
		 */
		try {
			data.putString("time", eventJson.getString("start_time"));
		} catch(JSONException e) {
			Log.e(TAG, "Critical error: Could not parse TLDatetime");
			return null;
		}
		
		// TODO Pass actual minimum age
		return new Event(data);
  }
  
  /**
   * Build a simple pair from the RSVP JSONObject
   * 
   * @param j The JSONObject to be parsed
   * 
   * @return A Pair representing the appropriate fields
   * 
   * @throws JSONException Go fuck yourself
   */
  private static Pair<String, String> getRSVP(JSONObject j) throws JSONException {
		if (j.has("url")) {
			return new Pair<String, String>("url", j.getString("url"));
		} else if (j.has("email")) {
			return new Pair<String, String>("email", j.getString("email"));
		} else {
			return new Pair<String, String>("", "");
		}
  }
  
  /**
   * TODO Implement method
   * @return
   */
  private static int getEnergyLevel() {
  	return MODERATE_ENERGY;
  }
  
  /**
   * TODO Implement method
   * @return
   */
  private static int getPriceLevel() {
  	return CHEAP;
  }

  /**
   * Build a simple GeoPoint from double
   * latitude and longitude. This should
   * arguably be a Pair<int, int> for
   * efficiency's sake
   * 
   * @param j The JSON to be parsed
   * 
   * @return A GeoPoint with int values
   * 
   * @throws JSONException Ballscock McWilliams
   */
  private static GeoPoint buildGeoPoint(final JSONObject j) throws JSONException {
  	final int lat = (int) (j.getDouble("latitude")*1E6);
  	final int lon = (int) (j.getDouble("longitude")*1E6);
  	return new GeoPoint(lat, lon);
  }
  
  /**
   * Nobody actually knows what this
   * method is useful for
   */
  public int describeContents() {
    return 0;
  }
  
  /**
   * Where objects go to die...
   */
  public String toString() {
    return "'" + this.name + "' radarCount=" + lineupCount + " onRadar=" + onLineup;
  }

  /**
   * The only fancy thing to do here
   * is make sure we remember whether
   * this event is on radar or not.
   * Technically this can be handled
   * outside of the Event class because
   * onLineup is a public member
   */
  public void writeToParcel(Parcel dest, int flags) {
  	data.putBoolean("onLineup", onLineup);
  	dest.writeBundle(data);
  }

  /**
   * Now that was easy.
   */
  public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
    public Event createFromParcel(Parcel in) {
    	return new Event(in.readBundle());
    }

    public Event[] newArray(int size) {
      return new Event[size];
    }
  };
}
