package com.tabbie.android.radar.maps;

import com.google.android.maps.OverlayItem;
import com.tabbie.android.radar.Event;

public class TLEventMarker extends OverlayItem {
	protected final Event event;
	
	public TLEventMarker(final Event e) {
		super(e.location, e.name, e.description);
		this.event = e;
	}
}