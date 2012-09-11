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
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class EventListController implements Parcelable {
	
  public static final int REQUEST_RETRIEVE_INSTANCE = 1;
  public static final String TAG = "EventsListController";
  
  public final Map<String, Event> eventsMap = new LinkedHashMap<String, Event>();
  
  public final List<Event> featured;
  public final List<Event> events;
  public final List<Event> lineup;
  
  public EventListController() {
	  events = new ArrayList<Event>();
	  featured = new ArrayList<Event>();
	  lineup = new ArrayList<Event>();
  }

  /** Sort by the number of people who have added
   * the event to their radar in REVERSE order
   */
  public static final class DefaultComparator implements Comparator<Event> {
		@Override
		public int compare(Event e1, Event e2) {
      if (e1.lineupCount > e2.lineupCount) {
        return -1;
      } else if (e1.lineupCount < e2.lineupCount) {
        return 1;
      }
      return 0;
		}
  }
  
  /** Sort by the start time of the event 
   */
  public static final class ChronologicalComparator implements Comparator<Event> {
		@Override
		public int compare(Event e1, Event e2) {
		  return e1.time.compareTo(e2.time);
		}
  }
  
  public void add(final Event e) {
		events.add(e);
		eventsMap.put(e.id, e);
		if(e.isFeatured) {
			featured.add(e);
		}
		if(e.onLineup) {
			lineup.add(e);
		}
  }

  /** Clear the controller. Use this method
   * instead of instantiating a new object
   * so that adapters retain their references
   */
  public void clear() {
    featured.clear();
    events.clear();
    lineup.clear();
  }

  /** Add the event to the lineup and keep
   * a reference to it.
   * 
   * @param e The event to add
   * @return Whether the event was successfully
   * added to the lineup or not
   */
  public boolean addToLineup(final Event e) {
    lineup.add(e);
    ++e.lineupCount;
    e.onLineup = true;
    return true;
  }
  
  /** Remove the given event from the lineup
   * 
   * @param e The event to remove
   * @return Whether the event was removed or not
   */
  public boolean removeFromLineup(final Event e) {
	    lineup.remove(e);
	    --e.lineupCount;
	    e.onLineup = false;
	    return true;
  }
  
  public int describeContents() {
    return 0;
  }
  
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(events);
  }

  public static final Parcelable.Creator<EventListController> CREATOR = new Parcelable.Creator<EventListController>() {
    public EventListController createFromParcel(Parcel in) {
      final List<Event> events = new ArrayList<Event>();
      in.readTypedList(events, Event.CREATOR);
      final EventListController c = new EventListController();
      for (final Event e : events) {
        c.add(e);
      }
      return c;
    }

    public EventListController[] newArray(int size) {
      return new EventListController[size];
    }
  };
}
