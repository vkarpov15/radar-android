package com.tabbie.android.radar;

import java.util.ArrayList;
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
}
