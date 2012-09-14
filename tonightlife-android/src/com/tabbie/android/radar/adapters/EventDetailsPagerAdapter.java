package com.tabbie.android.radar.adapters;

/**
 *  EventDetailsPagerAdapter.java
 * 
 *  Created on: Aug 12, 2012
 *      Author: Justin Knutson
 * 
 *  Custom adapter to scroll through multiple
 *  events from the EventDetailsActivity with
 *  unimplemented methods for Tab Bar integration
 */

import java.util.ArrayList;

import com.tabbie.android.radar.ImageLoader;
import com.tabbie.android.radar.R;
import com.tabbie.android.radar.R.anim;
import com.tabbie.android.radar.R.id;
import com.tabbie.android.radar.model.Event;

import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsPagerAdapter
	extends android.support.v4.view.PagerAdapter {
	
	private final ImageLoader imageLoader;
	private final Context context;
	private final ArrayList<Event> events;
	private final int pageLayout;
	private final OnClickListener clickListener;
	private final OnPageChangeListener pageListener;
	
	public EventDetailsPagerAdapter(final Context context,
	                                final ArrayList<Event> events,
	                                final int pageLayout,
	                                final OnClickListener listener) {
		
		this.context = context;
		this.clickListener = listener;
		imageLoader = new ImageLoader(context);
		this.events = events;
		this.pageLayout = pageLayout;
		this.pageListener = (OnPageChangeListener) context;
	}
	
	@Override
	public Object instantiateItem(android.view.ViewGroup container, int position) {
		Log.d("EventDetailsPagerAdapter", "Adding View at position " + position);
		final Event e = events.get(position);
		final View v = bindEvent(e);
		container.addView(v);
		pageListener.onPageChanged(e);
		return v;
	};
	
	
	@Override
	public void destroyItem(android.view.ViewGroup container, int position, Object object) {
		Log.d("EventDetailsPagerAdapter", "Removing View at position " + position);
		container.removeView((View) object);
	};

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	private View bindEvent(final Event e) {
		
		// Acquire references to all of our views
		final View v = LayoutInflater.from(context).inflate(pageLayout, null);
		final View loaderView = v.findViewById(R.id.element_loader);
		final TextView titleView = (TextView) v.findViewById(R.id.details_event_title);
		final TextView timeView = (TextView) v.findViewById(R.id.details_event_time);
		final TextView locationView = (TextView) v.findViewById(R.id.details_event_location);
	    final TextView addressView = (TextView) v.findViewById(R.id.details_event_address);
	    final TextView descriptionView = (TextView) v.findViewById(R.id.details_event_description);
	    final ImageView radarButton = (ImageView) v.findViewById(R.id.add_to_radar_image);
	    final ImageView locationLinkView = (ImageView) v.findViewById(R.id.location_image);
	    final ImageView imageView = (ImageView) v.findViewById(R.id.details_event_img);

	    // Begin loading image into display
		imageLoader.displayImage(e.imageUrl.toString(), imageView);
	    loaderView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));
	    
	    // Set string values
	    titleView.setText(e.name);
	    timeView.setText(e.time.makeYourTime());
	    locationView.setText(e.venue);
	    addressView.setText(e.address);
	    descriptionView.setText(e.description);
	    
	    // Make sure hyper-links are in place
	    Linkify.addLinks(descriptionView, Linkify.WEB_URLS);
	    
	    // MapView link listeners
	    locationLinkView.setOnClickListener(clickListener);
	    addressView.setOnClickListener(clickListener);
	    
	    // Set RadarButton and listener
	    radarButton.setSelected(e.onLineup);
	    radarButton.setOnClickListener(clickListener);
	    
	    
	    // Make sure our main view has a reference
	    // to the event that's populating it
	    v.setTag(e);
	    
	    return v;
	}
	
	public interface OnPageChangeListener {
		public abstract void onPageChanged(final Event e);
	}
}