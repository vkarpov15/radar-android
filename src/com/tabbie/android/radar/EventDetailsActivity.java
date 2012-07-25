package com.tabbie.android.radar;

import java.io.IOException;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class EventDetailsActivity extends Activity {
  private Event e;
  private ImageView eventImage;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_details_activity);
    
    eventImage = (ImageView) findViewById(R.id.event_img);
    
    Bundle starter = getIntent().getExtras();
    if (null != starter && starter.containsKey("event")) {
      e = starter.getParcelable("event");
    } else {
      return;
    }
    
    Log.d("TEST", e.name);
    try {
      eventImage.setImageDrawable(Drawable.createFromStream(e.image.openStream(), "src"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
