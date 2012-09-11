package com.tabbie.android.radar.adapters;

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

import com.tabbie.android.radar.Event;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class LineupListAdapter extends EventListAdapter {
	public static final String TAG = "LineupListAdapter";

	public LineupListAdapter(final Context context, final List<Event> events) {
		super(context, events);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = super.getView(position, convertView, parent);
		
		// TODO Code specific to LineupListAdapter
		
		
		return convertView;
	}

}
