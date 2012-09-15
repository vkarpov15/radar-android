package com.tabbie.android.radar.model;

/**
 *  ListManager.java
 *
 *  Created on: September 11, 2012
 *      @Author: Justin Knutson
 *      
 *  Simple class to replace the old common
 *  controller. Manages display lists for
 *  List View adapters.
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ListManager {
  public ArrayList<Event> allEventsList = new ArrayList<Event>();
  public ArrayList<Event> featuredEventsList = new ArrayList<Event>();
  public ArrayList<Event> lineupEventsList = new ArrayList<Event>();

  public ListManager() {}

  public boolean addToLineup(Event e) {
    if (e.onLineup) {
      return false;
    }
    e.lineupCount++;
    e.onLineup = true;
    lineupEventsList.add(e);
    return true;
  }
  
  public void add(Event e) {
    allEventsList.add(e);
    if (e.isFeatured) {
      featuredEventsList.add(e);
    }
    if (e.onLineup) {
      lineupEventsList.add(e);
    }
  }

  public void addAll(final List<? extends Event> events) {
    for (Event e : events) {
      add(e);
    }
  }

  public void clear() {
    allEventsList.clear();
    featuredEventsList.clear();
    lineupEventsList.clear();
  }

  /**
   * Sort by the number of people who have added the event to their radar in
   * REVERSE order
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

  /**
   * Sort by the start time of the event
   */
  public static final class ChronologicalComparator implements
      Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
      return e1.time.compareTo(e2.time);
    }
  }
}
