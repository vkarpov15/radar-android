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
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class EventsListController implements Parcelable {
	
  public static final int REQUEST_RETRIEVE_INSTANCE = 1;
  public static final String TAG = "EventsListController";
  
  public final List<Event> featuredEventsList;
  public final List<Event> masterEventsList;
  public final List<Event> lineupEventsList;
  
  public EventsListController() {
	  masterEventsList = new ArrayList<Event>();
	  featuredEventsList = new ArrayList<Event>();
	  lineupEventsList = new ArrayList<Event>();
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

  /** Clear the controller. Use this method
   * instead of instantiating a new object
   * so that adapters retain their references
   */
  public void clear() {
    featuredEventsList.clear();
    masterEventsList.clear();
    lineupEventsList.clear();
  }

  /** Add the event to the lineup and keep
   * a reference to it.
   * 
   * @param e The event to add
   * @return Whether the event was successfully
   * added to the lineup or not
   */
  public boolean addToLineup(final Event e) {
    lineupEventsList.add(e);
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
	    lineupEventsList.remove(e);
	    --e.lineupCount;
	    e.onLineup = false;
	    return true;
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
  
  // TODO Delete or Modify this method
  public boolean hasNoEvents(final short index) {
	  switch(index) {
	  case 0:
		  return featuredEventsList.isEmpty();
	  case 1:
		  return masterEventsList.isEmpty();
	  case 2:
		  return lineupEventsList.isEmpty();
	  default:
		  return true;
	  }
  }
  
  public Event findEventByTag(final String id) {
  	for(final Event e : masterEventsList) {
  		if(e.id.compareTo(id)==0) {
  			return e;
  		}
  	}
  	return null;
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

  public static final Parcelable.Creator<EventsListController> CREATOR = new Parcelable.Creator<EventsListController>() {
    public EventsListController createFromParcel(Parcel in) {
      final List<Event> events = new ArrayList<Event>();
      in.readTypedList(events, Event.CREATOR);
      final EventsListController c = new EventsListController();
      for (final Event e : events) {
        c.masterEventsList.add(e);
        if(e.isFeatured) {
        	c.masterEventsList.add(e);
        }
        if(e.onLineup) {
        	c.lineupEventsList.add(e);
        }
      }
      return c;
    }

    public EventsListController[] newArray(int size) {
      return new EventsListController[size];
    }
  };
}
