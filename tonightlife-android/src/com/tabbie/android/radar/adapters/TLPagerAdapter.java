package com.tabbie.android.radar.adapters;

import java.util.List;

import com.tabbie.android.radar.model.Event;

public interface TLPagerAdapter {
  public boolean initializeWithList(List<? extends Event> e);
}
