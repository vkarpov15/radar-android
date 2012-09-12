package com.tabbie.android.radar.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.tabbie.android.radar.Event;
import com.tabbie.android.radar.EventDetailsActivity;
import com.tabbie.android.radar.R;

public class TLMapActivity extends MapActivity
	implements OnClickListener {
	private final int DEFAULT_ZOOM = 14;
	private final int OVERLAY_ZOOM = 16;
	private final GeoPoint DEFAULT_LOCATION = new GeoPoint((int) (40.736968 * 1E6), (int) (-73.989183 * 1E6));
	
  private ArrayList<Event> events;
  private MapController mapController;
  private TLItemizedOverlay mapOverlay;
  private Event selected = null;
  private MapView mapView;
  private MyLocationOverlay myLocationOverlay;
  private String token;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map_activity);

    final Bundle starter = getIntent().getExtras();
    
    // Re-instantiate our events list and token
    events = starter.getParcelableArrayList("events");
    token = starter.getString("token");

    // Obtain and set properties for our MapView
    mapView = (MapView) findViewById(R.id.my_map_view);
    mapView.setBuiltInZoomControls(true);
    mapController = mapView.getController();
    mapController.setZoom(OVERLAY_ZOOM);
    mapController.animateTo(DEFAULT_LOCATION);

    mapOverlay = new TLItemizedOverlay(mapView, this);
    mapOverlay.popUp.setOnClickListener(this);

    if (starter.containsKey("event")) {
      selected = starter.getParcelable("event");
    }

    List<Overlay> overlays = mapView.getOverlays();
    overlays.clear();

    myLocationOverlay = new MyLocationOverlay(this, mapView);

    for (final Event e : events) {
      if (null != selected && 0 == e.id.compareTo(selected.id)) {
        mapOverlay.addEventMarker(e,
            getResources().getDrawable(R.drawable.marker_highlight));
      } else {
        mapOverlay.addEventMarker(e,
            getResources().getDrawable(R.drawable.marker));
      }
    }

    overlays.add(myLocationOverlay);
    overlays.add(mapOverlay);
    mapView.postInvalidate();

    if (null != selected) {
      mapController.animateTo(selected.location);
      mapController.setZoom(DEFAULT_ZOOM);
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
		mapController.setZoom(DEFAULT_ZOOM);
		mapController.animateTo(myLocationOverlay.getMyLocation());
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