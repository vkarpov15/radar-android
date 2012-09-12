package com.tabbie.android.radar.maps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.tabbie.android.radar.Event;
import com.tabbie.android.radar.R;

public class TLMapController {
	
	private static final String TAG = "RadarMapController";

  private final MapView mapView;
  private final View popUp;
  private TLItemizedOverlay markersCollection;

  public TLMapController(MapView mapView, Context context) {
    this.mapView = mapView;
    this.setLatLon(40.736968, -73.989183);
    this.setZoom(14);
    popUp = LayoutInflater.from(context).inflate(R.layout.popup, null);
    this.markersCollection = new TLItemizedOverlay(this.mapView, popUp);
  }
  
  public void setOnClickListener(final OnClickListener listener) {
  	popUp.setOnClickListener(listener);
  }

  public void setZoom(int zoom) {
    this.mapView.getController().setZoom(zoom);
  }

  public void setLatLon(double lat, double lon) {
    this.mapView.getController().animateTo(
        new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)));
  }

  public void setLatLon(GeoPoint p) {
    if (null == p) {
      return;
    }
    this.mapView.getController().setCenter(p);
  }

  public void addEventMarker(Event e, Drawable markerImg) {
	  Log.d(TAG, "Adding drawable marker");
    TLEventMarker marker = new TLEventMarker(e);
    markerImg.setBounds(0, 0, markerImg.getIntrinsicWidth(),
        markerImg.getIntrinsicHeight());
    marker.setMarker(markersCollection.boundDrawable(markerImg));
    this.markersCollection.addOverlay(marker);
  }

  public ItemizedOverlay<TLEventMarker> getItemizedOverlay() {
    return markersCollection;
  }
}
