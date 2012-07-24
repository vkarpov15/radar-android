package com.tabbie.android.radar;

import java.net.URL;

public class Event {
	public final String id;
	public final String name;
	public final String description;
	public final String venueName;
	public final URL image;
	public final double lat;
	public final double lon;
	public final int radarCount;
	public final boolean featured;
	
	private boolean onRadar;
	
	public Event(String id, String name, String description, String venueName, URL image, double lat, double lon, int radarCount, boolean featured) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.venueName = venueName;
		this.image = image;
		this.lat = lat;
		this.lon = lon;
		this.radarCount = radarCount;
		this.featured = featured;
	}

	public boolean isOnRadar() {
		return onRadar;
	}

	public void setOnRadar(boolean onRadar) {
		this.onRadar = onRadar;
	}
}
