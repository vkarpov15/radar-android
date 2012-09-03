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

  private final Map<String, Event> featuredEventsMap;
  private final List<Event> featuredEventsList;

  private final Map<String, Event> masterEventsMap;
  private final List<Event> masterEventsList;
  
  private final Map<String, Event> lineupEventsMap;
  private final List<Event> lineupEventsList;
  
  private final Map<String, Integer>  eventIdToIndex  = new LinkedHashMap<String, Integer>();

  private final Set<String>           lineupIds        = new LinkedHashSet<String>();
  
  private final List<Event>           lineupList       = new ArrayList<Event>();
  
  public RadarCommonController() {
	  masterEventsMap = new LinkedHashMap<String, Event>();
	  masterEventsList = new ArrayList<Event>();
	  
	  featuredEventsMap = new LinkedHashMap<String, Event>();
	  featuredEventsList = new ArrayList<Event>();
	  
	  lineupEventsMap = new LinkedHashMap<String, Event>();
	  lineupEventsList = new ArrayList<Event>();
  }

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
		  return e1.getTime().compareTo(e2.getTime());
	  }
  };
  

  public void addEvent(final Event e) {
    masterEventsMap.put(e.getTag(), e);
    masterEventsList.add(e);
    
    			eventIdToIndex.put(e.getTag(), masterEventsList.size() - 1);
    			
    if (e.isFeatured()) {
      featuredEventsMap.put(e.getTag(), e);
      featuredEventsList.add(e);
    }
    if (e.isOnRadar()) {
      lineupList.add(e);
      lineupIds.add(e.getTag());
    }
  }
  

  public void order() {
    Collections.sort(masterEventsList,    chronoOrdering);
    Collections.sort(featuredEventsList,  defaultOrdering);
    Collections.sort(lineupList,     chronoOrdering);
  }
  

  public void clear() {
    masterEventsList.clear();
    featuredEventsList.clear();
    lineupList.clear();
    featuredEventsMap.clear();
    masterEventsMap.clear();
    lineupIds.clear();
  }
  

  public Event getEvent(String id) {
    return masterEventsMap.get(id);
  }
  

  public boolean isOnRadar(Event e) {
    return lineupIds.contains(e.getTag());
  }
  
  
  public int getIndexInEventList(Event e) {
    return eventIdToIndex.get(e.getTag());
  }

  
  public boolean addToRadar(final Event e) {
    if (lineupIds.contains(e.getTag())) {
    	Log.v("RadarCommonController", "Add to Radar Failed");
      return false;
    }
    lineupIds.add(e.getTag());
    lineupList.add(e);
    ++e.radarCount;
    Log.d("Radar Count", "" + e.radarCount);
    e.setOnRadar(true);
    return true;
  }
  

  public boolean removeFromRadar(Event e) {
    if (!lineupIds.contains(e.getTag())) {
      return false;
    }
    lineupIds.remove(e.getTag());
    // TODO: this is slow, improve
    lineupList.remove(e);
    --e.radarCount;
    e.setOnRadar(false);
    return true;
  }
  
  public List<Event> findListById(final int id) {
	  switch(id) {
	  case R.id.featured_event_list:
		  return featuredEventsList;
	  case R.id.all_event_list:
		  return masterEventsList;
	  case R.id.lineup_event_list:
		  return lineupList;
	  default:
		  return null;
	  }
  }
  
  public boolean hasNoEvents() {
	  return masterEventsMap.isEmpty();
  }
  
  public boolean hasNoLineupEvents() {
	  return lineupList.isEmpty();
  }
  
  public List<Event> getFeaturedList() {
	  return featuredEventsList;
  }
  
  public List<Event> getAllList() {
	  return masterEventsList;
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
    dest.writeTypedList(masterEventsList);
  }

  
  public static final Parcelable.Creator<RadarCommonController> CREATOR = new Parcelable.Creator<RadarCommonController>() {
    public RadarCommonController createFromParcel(Parcel in) {
      List<Event> events = new ArrayList<Event>();
      in.readTypedList(events, Event.CREATOR);
      RadarCommonController c = new RadarCommonController();
      c.clear();
      Log.d("RadarCommonController", "Events List Size: " + c.masterEventsList.size());
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
