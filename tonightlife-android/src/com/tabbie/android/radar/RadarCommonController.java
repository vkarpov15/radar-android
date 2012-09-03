package com.tabbie.android.radar;

/**
 *  RadarCommonController.java
 *
 *  Created on: July 22, 2012
 *      Author: Valeri Karpov
 *      
 *  Data structure for maintaining a collection of events for quick access to
 *  which events are featured, which are on radar, etc. Note that this class
 *  is NOT thread-safe.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RadarCommonController implements Parcelable {
	
  public static final int REQUEST_RETRIEVE_INSTANCE = 1;
  public static final int REQUEST_FIRE_EVENT = 2;

  private final Map<String, Event>    featured        = new LinkedHashMap<String, Event>();
  private final List<Event>           featuredList    = new ArrayList<Event>();

  private final Map<String, Event>    events          = new LinkedHashMap<String, Event>();
  private final Map<String, Integer>  eventIdToIndex  = new LinkedHashMap<String, Integer>();
  private final List<Event>           eventsList      = new ArrayList<Event>();

  private final Set<String>           radarIds        = new LinkedHashSet<String>();
  private final List<Event>           lineupList       = new ArrayList<Event>();

  /** Sort by the number of people who have added
   * the event to their radar in REVERSE order
   */
  private static final Comparator<Event> defaultOrdering = new Comparator<Event>() {
    public int compare(Event e1, Event e2) {
      if (e1.radarCount > e2.radarCount) {
        return -1;
      } else if (e1.radarCount < e2.radarCount) {
        return 1;
      }
      return 0;
    }
  };

  
  /** Sort by the start time of the event 
   */
  private static final Comparator<Event> chronoOrdering = new Comparator<Event>() {
	  public int compare(final Event e1, final Event e2) {
		  return e1.time.compareTo(e2.time);
	  }
  };
  

  public void addEvent(final Event e) {
    events.put(e.id, e);
    eventsList.add(e);
    eventIdToIndex.put(e.id, eventsList.size() - 1);
    if (e.featured) {
      featured.put(e.id, e);
      featuredList.add(e);
    }
    if (e.isOnRadar()) {
      lineupList.add(e);
      radarIds.add(e.id);
    }
  }
  

  public void order() {
    Collections.sort(eventsList,    chronoOrdering);
    Collections.sort(featuredList,  defaultOrdering);
    Collections.sort(lineupList,     chronoOrdering);
  }
  

  public void clear() {
    eventsList.clear();
    featuredList.clear();
    lineupList.clear();
    featured.clear();
    events.clear();
    radarIds.clear();
  }
  

  public Event getEvent(String id) {
    return events.get(id);
  }
  

  public boolean isOnRadar(Event e) {
    return radarIds.contains(e.id);
  }
  
  
  public int getIndexInEventList(Event e) {
    return eventIdToIndex.get(e.id);
  }

  
  public boolean addToRadar(final Event e) {
    if (radarIds.contains(e.id)) {
    	Log.v("RadarCommonController", "Add to Radar Failed");
      return false;
    }
    radarIds.add(e.id);
    lineupList.add(e);
    ++e.radarCount;
    Log.d("Radar Count", "" + e.radarCount);
    e.setOnRadar(true);
    return true;
  }
  

  public boolean removeFromRadar(Event e) {
    if (!radarIds.contains(e.id)) {
      return false;
    }
    radarIds.remove(e.id);
    // TODO: this is slow, improve
    lineupList.remove(e);
    --e.radarCount;
    e.setOnRadar(false);
    return true;
  }
  
  public List<Event> findListById(final int id) {
	  switch(id) {
	  case R.id.featured_event_list:
		  return featuredList;
	  case R.id.all_event_list:
		  return eventsList;
	  case R.id.lineup_event_list:
		  return lineupList;
	  default:
		  return null;
	  }
  }
  
  public boolean hasNoEvents() {
	  return events.isEmpty();
  }
  
  public boolean hasNoLineupEvents() {
	  return lineupList.isEmpty();
  }
  
  public List<Event> getFeaturedList() {
	  return featuredList;
  }
  
  public List<Event> getAllList() {
	  return eventsList;
  }
  
  public List<Event> getLineupList() {
	  return lineupList;
  }

  
  public int describeContents() {
	  // TODO Robust implementation
    return 0;
  }

  
  public void writeToParcel(Parcel dest, int flags) {
    // Technically all we need to do is write eventsList, and then reconstruct
    // on the other side
    dest.writeTypedList(eventsList);
  }

  
  public static final Parcelable.Creator<RadarCommonController> CREATOR = new Parcelable.Creator<RadarCommonController>() {
    public RadarCommonController createFromParcel(Parcel in) {
      List<Event> events = new ArrayList<Event>();
      in.readTypedList(events, Event.CREATOR);
      RadarCommonController c = new RadarCommonController();
      c.clear();
      Log.d("RadarCommonController", "Events List Size: " + c.eventsList.size());
      for (Event e : events) {
        c.addEvent(e);
      }
      return c;
    }

    public RadarCommonController[] newArray(int size) {
      return new RadarCommonController[size];
    }
  };
}
