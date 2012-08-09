package com.tabbie.android.radar;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class RadarMapActivity extends MapActivity {
  private RadarCommonController commonController;
  private RadarMapController mapController;
  private Event selected = null;

  private MapView mapView;
  private MyLocationOverlay myLocationOverlay;

  @Override
  public void onCreate(Bundle saved) {
    super.onCreate(saved);
    setContentView(R.layout.map_activity);

    Bundle starter = getIntent().getExtras();
    if (null != starter && starter.containsKey("controller")) {
      commonController = starter.getParcelable("controller");
    } else {
      // No controller, nothing to display
      this.finish();
      return;
    }

    mapView = (MapView) findViewById(R.id.my_map_view);
    mapView.setBuiltInZoomControls(true);

    mapController = new RadarMapController(commonController, mapView, this);

    if (starter.containsKey("event")) {
      selected = starter.getParcelable("event");
    }

    List<Overlay> overlays = mapView.getOverlays();
    overlays.clear();

    myLocationOverlay = new MyLocationOverlay(this, mapView);
    myLocationOverlay.enableMyLocation();
    

    for (final Event e : commonController.eventsList) {
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
    switch (item.getItemId()) {
    case R.id.zoom_to_me:
      mapController.setLatLon(myLocationOverlay.getMyLocation());
      mapController.setZoom(16);
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

}
