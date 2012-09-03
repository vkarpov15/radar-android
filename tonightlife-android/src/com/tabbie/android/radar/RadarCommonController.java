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

  private final Map<String, Event> featuredEventsMap;
  public final List<Event> featuredEventsList;

  private final Map<String, Event> masterEventsMap;
  public final List<Event> masterEventsList;
  
  private final Map<String, Event> lineupEventsMap;
  public final List<Event> lineupEventsList;
  
  private final Map<String, Integer>  eventIdToIndex  = new LinkedHashMap<String, Integer>();
  
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
  public static final Comparator<Event> defaultOrdering = new Comparator<Event>() {
    public int compare(Event e1, Event e2) {
      if (e1.lineupCount > e2.lineupCount) {
        return -1;
      } else if (e1.lineupCount < e2.lineupCount) {
        return 1;
      }
      return 0;
    }
  };
 
  /** Sort by the start time of the event 
   */
  public static final Comparator<Event> chronoOrdering = new Comparator<Event>() {
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
    
    if (e.isOnLineup()) {
    	lineupEventsMap.put(e.getTag(), e);
    	lineupEventsList.add(e);
    }
  }

  public void clear() {
    featuredEventsList.clear();
    masterEventsList.clear();
    lineupEventsList.clear();
    featuredEventsMap.clear();
    masterEventsMap.clear();
    lineupEventsMap.clear();
  }
  

  public Event getEvent(final String id) {
    return masterEventsMap.get(id);
  }
  

  public boolean isOnRadar(final Event e) {
    return lineupEventsMap.containsKey(e.getTag());
  }
  
  
  public int getIndexInEventList(final Event e) {
    return eventIdToIndex.get(e.getTag());
  }

  public boolean addToRadar(final Event e) {
	  
    if (lineupEventsMap.containsKey(e.getTag())) {
    	Log.e("RadarCommonController", "Add to Radar Failed");
      return false;
    }
    lineupEventsMap.put(e.getTag(), e);
    lineupEventsList.add(e);
    ++e.lineupCount;
    e.setOnLineup(true);
    return true;
  }
  

  public boolean removeFromRadar(final Event e) {
    if (lineupEventsMap.containsKey(e.getTag())) {
	    lineupEventsMap.remove(e.getTag());
	    lineupEventsList.remove(e);
	    --e.lineupCount;
	    e.setOnLineup(false);
	    return true;
    } else {
    	return false;
    }
  }
  
  public List<Event> findListById(final int id) {
	  switch(id) {
	  case R.id.featured_event_list:
		  return featuredEventsList;
	  case R.id.all_event_list:
		  return masterEventsList;
	  case R.id.lineup_event_list:
		  return lineupEventsList;
	  default:
		  return null;
	  }
  }
  
  public boolean hasNoEvents() {
	  return masterEventsMap.isEmpty();
  }
  
  public boolean hasNoLineupEvents() {
	  return lineupEventsList.isEmpty();
  }
  
  public List<Event> getFeaturedList() {
	  return featuredEventsList;
  }
  
  public List<Event> getAllList() {
	  return masterEventsList;
  }
  
  public List<Event> getLineupList() {
	  return lineupEventsList;
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
