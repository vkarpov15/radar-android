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

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tabbie.android.radar.http.ServerDeleteRequest;
import com.tabbie.android.radar.http.ServerPostRequest;
import com.tabbie.android.radar.http.ServerResponse;

public class EventDetailsActivity extends ServerThreadActivity
	implements EventDetailsPagerAdapter.RadarSelectedListener {
	
  private Event e;
  private RadarCommonController commonController;
  private UnicornSlayerController tutorialController;
  private String token;

  private ImageView eventImage;
  private Bitmap image;

  private boolean tutorialMode = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_details_activity);

    tutorialController = new UnicornSlayerController(new AlertDialog.Builder(this));

    Bundle starter = getIntent().getExtras();
    if (null != starter && starter.containsKey("eventId")) {
      final String eventId = starter.getString("eventId");
      commonController = starter.getParcelable("controller");
      e = commonController.getEvent(eventId);
      token = starter.getString("token");
      image = starter.getParcelable("image");
      tutorialMode = starter.getBoolean("virgin", false);
    } else {
      // No event, nothing to display
      // Also, fatal error currently
      this.finish();
      return;
    }
    
    // TODO Code added by Justin
    final ViewPager pager = (ViewPager) findViewById(R.id.details_event_pager);
    new EventDetailsPagerAdapter(this, commonController, R.layout.event_details_element, pager);
    pager.setCurrentItem(commonController.eventsList.indexOf(e));
    
    /*
    eventImage = (ImageView) findViewById(R.id.details_event_img);
    eventImage.setImageBitmap(image);

    ((TextView) findViewById(R.id.details_event_title)).setText(e.name);
    ((TextView) findViewById(R.id.details_event_time)).setText(e.time
        .makeYourTime());
    ((TextView) findViewById(R.id.details_event_location)).setText(e.venueName);
    ((TextView) findViewById(R.id.details_event_address)).setText(e.address);
    ((TextView) findViewById(R.id.details_event_num_radar)).setText(Integer
        .toString(e.radarCount));
    ((TextView) findViewById(R.id.details_event_description))
        .setText(e.description);
    Linkify.addLinks((TextView) findViewById(R.id.details_event_description),
        Linkify.WEB_URLS);
    
    ((ImageView) findViewById(R.id.location_image)).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		    Intent intent = new Intent(EventDetailsActivity.this,
		            RadarMapActivity.class);
		        intent.putExtra("controller", commonController);
		        intent.putExtra("event", e);
		        startActivity(intent);
		}
	});

    final TextView radarCount = (TextView) findViewById(R.id.details_event_num_radar);
    final ImageView radarButton = (ImageView) findViewById(R.id.add_to_radar_image);
    radarButton.setSelected(e.isOnRadar());

    radarButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (e.isOnRadar() && commonController.removeFromRadar(e)) {
          Log.v("EventDetailsActivity", "Removing event from radar");
          radarButton.setSelected(false);
          radarCount.setText(Integer.toString(e.radarCount));

          ServerDeleteRequest req = new ServerDeleteRequest(
              ServerThread.TABBIE_SERVER + "/mobile/radar/" + e.id
                  + ".json?auth_token=" + token, MessageType.ADD_TO_RADAR);

          serverThread.sendRequest(req);
        } else if (!e.isOnRadar()) {
          Log.v("EventDetailsActivity", "Adding event to radar");
          if (commonController.addToRadar(e)) {
            radarButton.setSelected(true);
            radarCount.setText(Integer.toString(e.radarCount));

            ServerPostRequest req = new ServerPostRequest(
                ServerThread.TABBIE_SERVER + "/mobile/radar/" + e.id + ".json",
                MessageType.ADD_TO_RADAR);
            req.params.put("auth_token", token);
            serverThread.sendRequest(req);
          } else {
            Toast.makeText(EventDetailsActivity.this,
                "You can only add 3 events to your radar!", 5000).show();
            return;
          }
        }
      }
    });

    if (tutorialMode) {
      tutorialController.showDetailsTutorial();
      tutorialMode = false;
    }*/

  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtra("controller", commonController);
    setResult(RESULT_OK, intent);
    super.onBackPressed();
  }

  @Override
  protected boolean handleServerResponse(ServerResponse resp) {
    // Assume that ADD_TO_RADAR and REMOVE_FROM_RADAR always succeed
    return false;
  }
  
  
	@Override
	public void onRadarSelected(final View v, final Event e) {
	    final TextView radarCount = (TextView) v.findViewById(R.id.details_event_num_radar);
	    final ImageView radarButton = (ImageView) v.findViewById(R.id.add_to_radar_image);
	
        if (e.isOnRadar() && commonController.removeFromRadar(e)) {
          Log.v("EventDetailsActivity", "Removing event from radar");
          radarButton.setSelected(false);
          radarCount.setText(Integer.toString(e.radarCount));

          ServerDeleteRequest req = new ServerDeleteRequest(
              ServerThread.TABBIE_SERVER + "/mobile/radar/" + e.id
                  + ".json?auth_token=" + token, MessageType.ADD_TO_RADAR);

          serverThread.sendRequest(req);
        } else if (!e.isOnRadar()) {
          Log.v("EventDetailsActivity", "Adding event to radar");
          if (commonController.addToRadar(e)) {
            radarButton.setSelected(true);
            radarCount.setText(Integer.toString(e.radarCount));

            ServerPostRequest req = new ServerPostRequest(
                ServerThread.TABBIE_SERVER + "/mobile/radar/" + e.id + ".json",
                MessageType.ADD_TO_RADAR);
            req.params.put("auth_token", token);
            serverThread.sendRequest(req);
          } else {
            Toast.makeText(EventDetailsActivity.this,
                "You can only add 3 events to your radar!", Toast.LENGTH_SHORT).show();
            return;
          }
        }
	}
}