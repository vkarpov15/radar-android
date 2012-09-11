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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tabbie.android.radar.http.ServerDeleteRequest;
import com.tabbie.android.radar.http.ServerPostRequest;

public class EventDetailsActivity extends Activity implements
	OnClickListener,
	EventDetailsPagerAdapter.OnPageChangeListener {
	
  public final String TAG = "EventDetailsActivity";
  private final Handler upstreamHandler;
  private ArrayList<Event> events;
	
  private Event e;
  private String token;
  private GoogleAnalyticsTracker googleAnalyticsTracker;
  
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
    googleAnalyticsTracker = GoogleAnalyticsTracker.getInstance();
    
    final Bundle starter = getIntent().getExtras();
    
    final int eventIndex = starter.getInt("eventIndex");
    events = starter.getParcelableArrayList("events");
    
    e = events.get(eventIndex);
    token = starter.getString("token");
    
    final ViewPager pager = (ViewPager) findViewById(R.id.details_event_pager);
		pager.setAdapter(new EventDetailsPagerAdapter(this, events, R.layout.event_details_element, this));
    pager.setCurrentItem(events.indexOf(e));

  }
  
  @Override
  protected void onStart() {
  	googleAnalyticsTracker.startNewSession("UA-34193317-1", 20, this);
  	super.onStart();
  }

  
  @Override
  protected void onStop() {
  	googleAnalyticsTracker.stopSession();
  	super.onStop();
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
	
	final Event e = (Event) ((View) v.getParent()).getTag();
	
	switch(v.getId()) {
	case R.id.details_event_address:
		Log.d(TAG, "Event Address Selected");
	case R.id.location_image:
		Log.d(TAG, "Location Image Selected");
		final Intent intent = new Intent(this, RadarMapActivity.class);
		intent.putParcelableArrayListExtra("events", events);
		intent.putExtra("event", e);
		intent.putExtra("token", token);
		startActivity(intent);
		break;
		
	case R.id.add_to_radar_image:
		Log.d(TAG, "Lineup Button Selected");
    final ImageView radarButton = (ImageView) v.findViewById(R.id.add_to_radar_image);
      if (e.onLineup) {
      	e.onLineup = false;
        radarButton.setSelected(false);
        final ServerDeleteRequest req = new ServerDeleteRequest(
            getString(R.string.tabbie_server) + "/mobile/radar/" + e.id
                + ".json?auth_token=" + token, MessageType.ADD_TO_RADAR);
        final Message message = Message.obtain();
        message.obj = req;
        upstreamHandler.sendMessage(message);
      } else {
      	e.onLineup = true;
        radarButton.setSelected(true);
        ServerPostRequest req = new ServerPostRequest(
        		getString(R.string.tabbie_server) + "/mobile/radar/" + e.id + ".json",
            MessageType.ADD_TO_RADAR);
        req.params.put("auth_token", token);
        final Message message = Message.obtain();
        message.obj = req;
        upstreamHandler.sendMessage(message);
      }
      break;
		}
	}

	@Override
	public void onPageChanged(Event e) {
  	googleAnalyticsTracker.trackPageView(e.name);
	}
}