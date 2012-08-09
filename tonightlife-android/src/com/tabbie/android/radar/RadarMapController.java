package com.tabbie.android.radar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;

public class RadarMapController {
  private final RadarCommonController commonController;

  private double lat;
  private double lon;
  private int zoom;

  private final MapView mapView;
  private final Context context;
  
  private class TabbieEventMarkerCollection extends
      ItemizedOverlay<EventMarker> {
    private final List<EventMarker> markers = new ArrayList<EventMarker>();
    private long lastClickTime = -1;

    public TabbieEventMarkerCollection() {
      super(null); // TODO Give a default drawable here
    }

    public void addOverlay(EventMarker overlay) {
      markers.add(overlay);
      populate();
    }

    @Override
    protected EventMarker createItem(int i) {
      return markers.get(i);
    }

    @Override
    public int size() {
      return markers.size();
    }

    protected boolean onTap(int index) {
      final EventMarker m = markers.get(index);
      final View popUp = LayoutInflater.from(context).inflate(R.layout.popup, null);
      MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
    		  ViewGroup.LayoutParams.WRAP_CONTENT,
    		  m.getPoint(),
    		  0,
    		  0,
    		  MapView.LayoutParams.BOTTOM_CENTER);
      mapView.addView(popUp, mapParams);
      return true;
    }

    public Drawable boundDrawable(Drawable drawable) {
      return boundCenterBottom(drawable);
    }

    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
      if (MotionEvent.ACTION_DOWN == event.getAction()) {
        if ((System.currentTimeMillis() - lastClickTime) < 750) {
          mapView.getController().zoomIn();
        }
        lastClickTime = System.currentTimeMillis();
      }
      return super.onTouchEvent(event, mapView);
    }
  }

  private TabbieEventMarkerCollection markersCollection;

  public RadarMapController(RadarCommonController commonController,
      MapView mapView, Context context) {
    this.commonController = commonController;
    this.lat = 0;
    this.lon = 0;
    this.zoom = 0;
    this.mapView = mapView;
    this.setLatLon(40.736968, -73.989183);
    this.setZoom(14);
    this.markersCollection = new TabbieEventMarkerCollection();
    this.context = context;
  }

  public void setZoom(int zoom) {
    this.zoom = zoom;
    this.mapView.getController().setZoom(zoom);
  }

  public void setLatLon(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
    this.mapView.getController().setCenter(
        new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)));
  }

  public void setLatLon(GeoPoint p) {
    if (null == p) {
      return;
    }
    this.lat = ((double) p.getLatitudeE6()) / 1E6;
    this.lon = ((double) p.getLongitudeE6()) / 1E6;
    this.mapView.getController().setCenter(p);
  }

  public void addEventMarker(Event e, Drawable markerImg) {
    EventMarker marker = new EventMarker(e);
    markerImg.setBounds(0, 0, markerImg.getIntrinsicWidth(),
        markerImg.getIntrinsicHeight());
    marker.setMarker(markersCollection.boundDrawable(markerImg));
    this.markersCollection.addOverlay(marker);
  }

  public ItemizedOverlay<EventMarker> getItemizedOverlay() {
    return markersCollection;
  }

}
