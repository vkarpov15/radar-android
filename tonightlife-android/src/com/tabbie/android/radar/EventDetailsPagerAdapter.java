package com.tabbie.android.radar;

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

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsPagerAdapter
	extends android.support.v4.view.PagerAdapter
	implements ViewPager.OnPageChangeListener {
	
	private final ImageLoader imageLoader;
	private final Context context;
	private final RadarCommonController controller;
	private final int pageLayout;
	private final OnClickListener listener;
	
	public EventDetailsPagerAdapter(final Context context,
	                                final RadarCommonController controller,
	                                final int pageLayout,
	                                final ViewPager pager,
	                                final OnClickListener listener) {
		
		this.context = context;
		this.listener = listener;
		imageLoader = new ImageLoader(context);
		this.controller = controller;
		this.pageLayout = pageLayout;
		pager.setAdapter(this);
		pager.setOnPageChangeListener(this);
	}
	
	@Override
	public Object instantiateItem(android.view.ViewGroup container, int position) {
		Log.d("EventDetailsPagerAdapter", "Adding View at position " + position);
		final Event e = controller.getAllList().get(position);
		final View v = bindEvent(e);
		container.addView(v);
		return v;
	};
	
	
	@Override
	public void destroyItem(android.view.ViewGroup container, int position, Object object) {
		Log.d("EventDetailsPagerAdapter", "Removing View at position " + position);
		container.removeView((View) object);
	};

	@Override
	public int getCount() {
		return controller.getAllList().size();
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
		imageLoader.displayImage(e.getUrl().toString(), imageView);
	    loaderView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));
	    
	    // Set string values
	    titleView.setText(e.getName());
	    timeView.setText(e.getTime().makeYourTime());
	    locationView.setText(e.getVenueName());
	    addressView.setText(e.getAddress());
	    descriptionView.setText(e.getDescription());
	    
	    // Make sure hyper-links are in place
	    Linkify.addLinks(descriptionView, Linkify.WEB_URLS);
	    
	    // MapView link listeners
	    locationLinkView.setOnClickListener(listener);
	    addressView.setOnClickListener(listener);
	    
	    // Set RadarButton and listener
	    radarButton.setSelected(e.isOnLineup());
	    radarButton.setOnClickListener(listener);
	    
	    
	    // Make sure our main view has a reference
	    // to the event that's populating it
	    v.setTag(e);
	    
	    return v;
	}

	
	// 		These methods give us access to
	//		the action bar/tab bar if we ever
	//		want to create such a display.
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		
	}
}
