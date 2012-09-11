package com.tabbie.android.radar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListManager {
	public ArrayList<Event> allEventsList = new ArrayList<Event>();
	public ArrayList<Event> featuredEventsList = new ArrayList<Event>();
	public ArrayList<Event> lineupEventsList = new ArrayList<Event>();
	
	public ListManager() {}

	public ListManager(final List<? extends Event> events) {
		allEventsList.addAll(events);
		for(final Event e : events) {
			if(e.isFeatured) {
				featuredEventsList.add(e);
			}
			if(e.onLineup) {
				lineupEventsList.add(e);
			}
		}
	}
	
	public void addAll(final List<? extends Event> events) {
		allEventsList.addAll(events);
		for(final Event e : events) {
			if(e.isFeatured) {
				featuredEventsList.add(e);
			}
			if(e.onLineup) {
				lineupEventsList.add(e);
			}
		}
	}
	
	public void clear() {
		allEventsList.clear();
		featuredEventsList.clear();
		lineupEventsList.clear();
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
}
