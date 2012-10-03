package com.tabbie.android.radar;

import java.util.Comparator;

import com.tabbie.android.radar.model.Event;

public class DefaultComparator implements Comparator<Event> {
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
