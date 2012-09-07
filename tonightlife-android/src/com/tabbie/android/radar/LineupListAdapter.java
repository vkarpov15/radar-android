package com.tabbie.android.radar;

/**
 *  LineupListAdapter.java
 *
 *  Created on: September 7, 2012
 *      @author: Justin Knutson
 *      
 *  An extension of EventListAdapter that is
 *  equipped to deal with share messages of
 *  the type ShareMessage.class
 */

import java.util.List;

import android.content.Context;

public class LineupListAdapter extends EventListAdapter {

	public LineupListAdapter(final Context context, final List<Event> events) {
		super(context, events);
		// TODO Auto-generated constructor stub
	}

}
