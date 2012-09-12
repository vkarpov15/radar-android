package com.tabbie.android.radar.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.tabbie.android.radar.Event;

public class TLEventMarker extends OverlayItem {
	private final Event event;
	
	public interface OnClickListener {
		public void onClick();
	}
	
	public TLEventMarker(final Event e) {
		super(new GeoPoint((int) (e.lat*1E6),
				(int) (e.lon*1E6)),
				e.name, e.description);
		this.event = e;
	}
	
	public String getTitle() {
		return event.name;
	}
	
	public Event getEvent() {
		return event;
	}
}
