package com.tabbie.android.radar;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class EventMarker extends OverlayItem {
	private final Event event;
	
	public interface OnClickListener {
		public void onClick();
	}
	
	public EventMarker(final Event e) {
		super(new GeoPoint((int) (e.getLatitude()*1E6),
				(int) (e.getLongitude()*1E6)),
				e.getName(), e.getDescription());
		this.event = e;
	}
	
	public String getTitle() {
		return event.getName();
	}
	
	public Event getEvent() {
		return event;
	}
}
