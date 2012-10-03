package com.tabbie.android.radar;

public enum Lists {
	FEATURED(0, "Featured"),
	ALL(1, "All"),
	LINEUP(2, "Lineup");
	
	public String id;
	public int index;
	
	Lists(int index, String id) {
		this.id = id;
		this.index = index;
	}
}
