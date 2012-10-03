package com.tabbie.android.radar;

import java.util.Comparator;

import com.tabbie.android.radar.model.Event;

public class ChronologicalComparator implements Comparator<Event> {
  @Override
  public int compare(Event e1, Event e2) {
    return e1.time.compareTo(e2.time);
  }
}
