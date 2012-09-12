package com.tabbie.android.radar;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TLMapActivity extends MapActivity
	implements OnClickListener {
	
  private ArrayList<Event> events;
  private MapController mapController;
  private Event selected = null;

  private MapView mapView;
  private MyLocationOverlay myLocationOverlay;
  private String token;

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    setContentView(R.layout.map_activity);

    Bundle starter = getIntent().getExtras();
    
    events = starter.getParcelableArrayList("events");
    token = starter.getString("token");

    mapView = (MapView) findViewById(R.id.my_map_view);
    mapView.setBuiltInZoomControls(true);

    mapController = new MapController(mapView, this);
    mapController.setOnClickListener(this);

    if (starter.containsKey("event")) {
      selected = starter.getParcelable("event");
    }

    List<Overlay> overlays = mapView.getOverlays();
    overlays.clear();

    myLocationOverlay = new MyLocationOverlay(this, mapView);

    for (final Event e : events) {
      if (null != selected && 0 == e.id.compareTo(selected.id)) {
        mapController.addEventMarker(e,
            getResources().getDrawable(R.drawable.marker_highlight));
      } else {
        mapController.addEventMarker(e,
            getResources().getDrawable(R.drawable.marker));
      }
    }

    overlays.add(myLocationOverlay);
    overlays.add(mapController.getItemizedOverlay());
    mapView.postInvalidate();

    if (null != selected) {
      mapController.setLatLon(selected.lat, selected.lon);
      mapController.setZoom(16);
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    myLocationOverlay.enableMyLocation();
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    myLocationOverlay.disableMyLocation();
  }

  @Override
  protected boolean isRouteDisplayed()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
	if (itemId == R.id.zoom_to_me) {
		mapController.setLatLon(myLocationOverlay.getMyLocation());
		mapController.setZoom(16);
		return true;
	} else {
		return super.onOptionsItemSelected(item);
	}
  }

	@Override
	public void onClick(final View v) {
		
		final Event e = (Event) v.getTag();
		Intent intent = new Intent(this, EventDetailsActivity.class);
	    intent.putExtra("eventId", e.id);
	    intent.putParcelableArrayListExtra("events", events);
	    intent.putExtra("token", token);
	    startActivity(intent);
	}
}
