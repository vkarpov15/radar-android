package com.tabbie.android.radar;

/*
 *  EventDetailsActivity.java
 *
 *  Created on: July 25, 2012
 *      Author: Valeri Karpov
 *      
 *  Super simple activity for displaying a more detailed view of an event.
 *  All we do is just set a bunch of layout views to match our event model
 */

import java.io.IOException;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsActivity extends Activity {
  private Event e;
  private ImageView eventImage;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_details_activity);
    
    eventImage = (ImageView) findViewById(R.id.details_event_img);
    
    
    Bundle starter = getIntent().getExtras();
    if (null != starter && starter.containsKey("event")) {
      e = starter.getParcelable("event");
    } else {
      // No event, nothing to display
      this.finish();
      return;
    }
    
    try {
      eventImage.setImageDrawable(Drawable.createFromStream(e.image.openStream(), "src"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    ((TextView) findViewById(R.id.details_event_title)).setText(e.name);
    ((TextView) findViewById(R.id.details_event_time)).setText(e.time);
    ((TextView) findViewById(R.id.details_event_location)).setText(e.venueName);
    ((TextView) findViewById(R.id.details_event_num_radar)).setText(Integer.toString(e.radarCount));
    ((TextView) findViewById(R.id.details_event_description)).setText(e.description);
  }
}
