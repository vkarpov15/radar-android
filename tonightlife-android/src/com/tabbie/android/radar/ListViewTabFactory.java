package com.tabbie.android.radar;

/**
 *  ListViewTabFactory.java
 *
 *  Created on: September 2, 2012
 *      Author: Justin Knutson
 *      
 *  Convenience class for building
 *  tabs that have ListViews as their
 *  member objects
 */

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;



public class ListViewTabFactory {
	
	public static final void createTabView(final TabHost host, final ListView view) {
		final String tag = view.getTag().toString();
		final View tabIndicatorView = LayoutInflater.from(host.getContext())
				.inflate(R.layout.tabs_bg, null); 
		((TextView) tabIndicatorView.findViewById(R.id.tabs_text)).setText(tag);
		
        final TabSpec content = host.newTabSpec(tag)
        		.setIndicator(tabIndicatorView)
        		.setContent(new TabHost.TabContentFactory() {
        			public View createTabContent(final String tag) {
        				return view;
        			}
        	});
        host.addTab(content);
	}

}
