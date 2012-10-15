package com.tabbie.android.radar;

/**
 *  EventDetailsActivity.java
 *
 *  Created on: July 25, 2012
 *      Author: Valeri Karpov
 *      
 *  Super simple activity for displaying a more detailed view of an event.
 *  All we do is just set a bunch of layout views to match our event model
 */

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.android.apps.analytics.easytracking.EasyTracker;
import com.google.android.apps.analytics.easytracking.TrackedActivity;
import com.tabbie.android.radar.adapters.EventDetailsPagerAdapter;
import com.tabbie.android.radar.enums.MessageType;
import com.tabbie.android.radar.http.GenericServerDeleteRequest;
import com.tabbie.android.radar.http.GenericServerPostRequest;
import com.tabbie.android.radar.http.ServerThreadHandler;
import com.tabbie.android.radar.maps.TLMapActivity;
import com.tabbie.android.radar.model.Event;

public class EventDetailsActivity extends TrackedActivity implements
	OnClickListener,
	OnPageChangeListener {
	
  public static final String TAG = "EventDetailsActivity";
  private final Handler upstreamHandler;
  private ArrayList<Event> events;
  private ArrayList<Event> childEventsList;
	
  private String token;
  
  public EventDetailsActivity() {
	  super();
	  final HandlerThread serverThread = new HandlerThread(TAG + "Thread");
	  serverThread.start();
	  upstreamHandler = new ServerThreadHandler(serverThread.getLooper());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_details_activity);
    
    final Bundle starter = getIntent().getExtras();
    
    final int eventIndex = starter.getInt("eventIndex");
    events = starter.getParcelableArrayList("events");
    childEventsList = starter.getParcelableArrayList("childList");
    token = starter.getString("token");
    
    final ViewPager pager = (ViewPager) findViewById(R.id.details_event_pager);
    pager.setAdapter(new EventDetailsPagerAdapter(this, childEventsList, R.layout.event_details_element, this));
    pager.setCurrentItem(eventIndex);
    pager.setOnPageChangeListener(this);
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.putParcelableArrayListExtra("events", events);
    setResult(RESULT_OK, intent);
    super.onBackPressed();
  }
  
  @Override
  public void onClick(View v) {
  	View parent = (View) v.getParent();
  	Event e = null;
  	for(Event event : events) {
  		if(event.id.contentEquals(((Event) parent.getTag()).id)) {
  			e = event;
  		}
  	}
  	Log.d(TAG, "Event clicked is: " + e.name);
  	
  	switch(v.getId()) {
  	case R.id.details_event_address:
  		Log.d(TAG, "Event Address Selected");
  	case R.id.location_image:
  		Log.d(TAG, "Location Image Selected");
  		final Intent intent = new Intent(this, TLMapActivity.class);
  		intent.putParcelableArrayListExtra("events", events);
  		intent.putExtra("eventIndex", events.indexOf(e));
  		intent.putExtra("token", token);
  		startActivity(intent);
  		break;
  		
  	case R.id.add_to_radar_image:
  		Log.d(TAG, "Lineup Button Selected");
      final ImageView radarButton = (ImageView) v.findViewById(R.id.add_to_radar_image);
      if (e.onLineup) {
      	e.lineupCount--;
       	e.onLineup = false;
        radarButton.setSelected(false);
        GenericServerDeleteRequest req = new GenericServerDeleteRequest(MessageType.REMOVE_FROM_RADAR, e.id, token);
        final Message message = Message.obtain();
        message.obj = req;
        upstreamHandler.sendMessage(message);
      } else {
      	e.lineupCount++;
       	e.onLineup = true;
        radarButton.setSelected(true);
        GenericServerPostRequest req = new GenericServerPostRequest(MessageType.ADD_TO_RADAR, e.id);
        req.params.put("auth_token", token);
        final Message message = Message.obtain();
        message.obj = req;
        upstreamHandler.sendMessage(message);
      }
      break;
  	}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int position) {
	  final Event e = events.get(position);
	  EasyTracker.getTracker().trackEvent("Event", "Swipe", e.name, 1);
	}
}