package com.tabbie.android.radar.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

import com.tabbie.android.radar.R;
import com.tabbie.android.radar.core.facebook.FBPerson;
import com.tabbie.android.radar.model.Event;


public class TLJSONParser {
	public static final String JSON_KEY_LINEUP_IDS = "radar";
	public static final String JSON_KEY_LINEUP_COUNT = "user_count";
	public static final String JSON_KEY_NULL = "null";
	
	public static Set<String> parseLineupIds(JSONObject radarObj) throws JSONException {
		JSONArray tmpRadarList = radarObj.getJSONArray(JSON_KEY_LINEUP_IDS);
		int length = tmpRadarList.length();
		Set<String> serverLineupIds = new LinkedHashSet<String>(length);
		for(int i = 0; i < length; i++) {
			serverLineupIds.add(tmpRadarList.getString(i));
		}
		return serverLineupIds;
	}
	
	public static Event parseEvent(JSONObject obj,
			Context context, Set<String> serverLineupIds)
					throws JSONException, MalformedURLException {
		
		int lineupCount = parseLineupCount(obj.getString(JSON_KEY_LINEUP_COUNT));
		Pair<String, String> rsvp = parseRsvpMethod(obj.getJSONObject("rsvp"));
		final Event.Energy energyLevel = parseEnergyLevel(0); // TODO Implementation
		final Event.Price priceLevel = parsePriceLevel(0); // TODO Implementation
		
		return new Event(obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("description"),
                                obj.getString("location"),
                                obj.getString("street_address"),
                                new URL(context.getString(R.string.tabbie_server) + obj.getString("image_url")),
                                (int) (obj.getDouble("latitude")*1E6),
                                (int) (obj.getDouble("longitude")*1E6),
                                lineupCount,
                                obj.getBoolean("featured"),
                                obj.getString("start_time"),
                                serverLineupIds.contains(obj.getString("id")),
                                rsvp);
	}
	
	private static int parseLineupCount(String lineupCountString) {
		if (null != lineupCountString && 0 != lineupCountString.compareTo(JSON_KEY_NULL)) {
			return Integer.parseInt(lineupCountString);
		} else {
			return 0;
		}
	}
	
	private static Pair<String, String> parseRsvpMethod(JSONObject rsvpObj) throws JSONException {
		if (rsvpObj.has("url")) {
			return new Pair<String, String>("url", rsvpObj.getString("url"));
		} else if (rsvpObj.has("email")) {
			return new Pair<String, String>("email", rsvpObj.getString("email"));
		} else {
			return new Pair<String, String>("", "");
		}
	}
	
	private static Event.Energy parseEnergyLevel(int energy) {
		switch(energy) {
		case 0:
			return Event.Energy.LOW;
		case 1:
			return Event.Energy.MODERATE;
		case 2:
			return Event.Energy.HIGH;
		default:
			return Event.Energy.MODERATE;
		}
	}
	
	private static Event.Price parsePriceLevel(int price) {
		switch(price) {
		case 0:
			return Event.Price.FREE;
		case 1:
			return Event.Price.CHEAP;
		case 2:
			return Event.Price.EXPENSIVE;
		default:
			return Event.Price.CHEAP;
		}
	}
	
	public static HashMap<String, FBPerson> parseFacebookFriendsList(JSONArray json) {
		JSONObject currentPerson = null;
		int length = json.length();
		HashMap<String, FBPerson> friendsList = new LinkedHashMap<String, FBPerson>(length);
		for(int i = 0; i < length; i++) {
			try {
				currentPerson = (JSONObject) json.getJSONObject(i);
				friendsList.put(currentPerson.getString("id"),
						new FBPerson(currentPerson.getString("name"), currentPerson.getString("id")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return friendsList;
	}
	
	public static Set<String> parseFacebookIds(JSONArray ids) {
		int length = ids.length();
		Set<String> idSet = new LinkedHashSet<String>(length);
		for(int i = 0; i < length; i++) {
			try {
				idSet.add((String) ids.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
		return idSet;
	}
}
