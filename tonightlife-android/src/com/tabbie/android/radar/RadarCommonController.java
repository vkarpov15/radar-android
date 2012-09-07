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
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RadarCommonController implements Parcelable {
	
  public static final int REQUEST_RETRIEVE_INSTANCE = 1;
  public static final String TAG = "RadarCommonController";
  
  public static final short FEATURED = 0;
  public static final short ALL = 1;
  public static final short LINEUP = 2;

  private final Map<String, Event> featuredEventsMap;
  private final Map<String, Event> masterEventsMap;
  private final Map<String, Event> lineupEventsMap;
  
  private final List<Event> featuredEventsList;
  private final List<Event> masterEventsList;
  private final List<Event> lineupEventsList;
  
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
  private static final Comparator<Event> chronoOrdering = new Comparator<Event>() {
	  public int compare(final Event e1, final Event e2) {
		  return e1.getTime().compareTo(e2.getTime());
	  }
  };
  
  /** Sort the specified list according
   * to its pre-coded ordering
   * 
   * @param index Use the built in
   * controller indices
   */
  public void sort(final short index) {
	  switch(index) {
	  case FEATURED:
		  Collections.sort(featuredEventsList, defaultOrdering);
		  break;
	  case ALL:
		  Collections.sort(masterEventsList, defaultOrdering);
		  break;
	  case LINEUP:
		  Collections.sort(lineupEventsList, chronoOrdering);
		  break;
	  default:
		  break;
	  }
  }
  
  /** Add an event to the appropriate lists. These
   * will not be garbage collected until the controller
   * is destroyed.
   * 
   * 
   * @param e The newly instantiated event
   */
  public void addEvent(final Event e) {
    masterEventsMap.put(e.getTag(), e);
    masterEventsList.add(e);
    			
    if (e.isFeatured()) {
      featuredEventsMap.put(e.getTag(), e);
      featuredEventsList.add(e);
    }
    
    if (e.isOnLineup()) {
    	lineupEventsMap.put(e.getTag(), e);
    	lineupEventsList.add(e);
    }
  }

  /** Clear the controller. Use this method
   * instead of instantiating a new object
   * so that adapters retain their references
   */
  public void clear() {
    featuredEventsList.clear();
    masterEventsList.clear();
    lineupEventsList.clear();
    
    featuredEventsMap.clear();
    masterEventsMap.clear();
    lineupEventsMap.clear();
  }
  
  /** Find an event by its tag
   * 
   * @param tag The tag corresponding to this event
   * @return The event
   */
  public Event findEventByTag(final String tag) {
    return masterEventsMap.get(tag);
  }

  /** Add the event to the lineup and keep
   * a reference to it.
   * 
   * @param e The event to add
   * @return Whether the event was successfully
   * added to the lineup or not
   */
  public boolean addToLineup(final Event e) {
    if (lineupEventsMap.containsKey(e.getTag())) {
      Log.e("RadarCommonController", "Add to Lineup Failed");
      return false;
    }
    lineupEventsMap.put(e.getTag(), e);
    lineupEventsList.add(e);
    ++e.lineupCount;
    e.setOnLineup(true);
    return true;
  }
  
  /** Remove the given event from the lineup
   * 
   * @param e The event to remove
   * @return Whether the event was removed or not
   */
  public boolean removeFromLineup(final Event e) {
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
  
  /** Find the appropriate list based on the ID
   * specified in the R file
   * 
   * @param id The R file ID of the event list
   * @return The list or null if no list is found
   */
  public List<Event> findListById(final int id) {
	  switch(id) {
	  // TODO Abstractify
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
  
  public boolean hasNoEvents(final short index) {
	  switch(index) {
	  case FEATURED:
		  return featuredEventsList.isEmpty();
	  case ALL:
		  return masterEventsMap.isEmpty();
	  case LINEUP:
		  return lineupEventsList.isEmpty();
	  default:
		  return true;
	  }
  }
  
  /** Retrieve the master events list.
   * This method is used for keeping track of
   * indices in the Details Activity
   * 
   * @return The master events list
   */
  public List<Event> getMasterList() {
	  return masterEventsList;
  }
  
  public int describeContents() {
    return 0;
  }
  
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(masterEventsList);
  }

  public static final Parcelable.Creator<RadarCommonController> CREATOR = new Parcelable.Creator<RadarCommonController>() {
    public RadarCommonController createFromParcel(Parcel in) {
      final List<Event> events = new ArrayList<Event>();
      in.readTypedList(events, Event.CREATOR);
      final RadarCommonController c = new RadarCommonController();
      for (final Event e : events) {
        c.addEvent(e);
      }
      return c;
    }

    public RadarCommonController[] newArray(int size) {
      return new RadarCommonController[size];
    }
  };
}
