package com.tabbie.android.radar.maps;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.tabbie.android.radar.Event;
import com.tabbie.android.radar.R;

public final class TLItemizedOverlay extends ItemizedOverlay<TLEventMarker> {
	protected final ArrayList<TLEventMarker> markers = new ArrayList<TLEventMarker>();
  private long lastClickTime = -1;
  private final MapView mapView;
  private final View popUp;

	public TLItemizedOverlay(final MapView mapView, final View popUp) {
		super(null);
		this.mapView = mapView;
		this.popUp = popUp;
	}

  @Override
  protected TLEventMarker createItem(int i) {
    return markers.get(i);
  }

  @Override
  public int size() {
    return markers.size();
  }
	
	@Override
	protected boolean onTap(int index) {
  	// TODO Launch eventdetails intents here when the drawables shit is fixed
  	Log.d("ASDF", "I clicked " + index);
  	mapView.removeView(popUp);
    final TLEventMarker m = markers.get(index);
    final Event e = m.event;
    final MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
  		  ViewGroup.LayoutParams.WRAP_CONTENT,
  		  m.getPoint(),
  		  0,
  		  -35, // TODO Probably shouldn't be hard-coded, but I don't quite know how this works anyways
  		  MapView.LayoutParams.BOTTOM_CENTER);
    ((TextView) popUp.findViewById(R.id.map_event_title)).setText(e.name);
    ((TextView) popUp.findViewById(R.id.map_event_time)).setText(e.time.makeYourTime());
    mapView.getController().animateTo(
        new GeoPoint((int) (e.lat * 1E6), (int) (e.lon * 1E6)));
    
    popUp.setTag(e);
    
    mapView.addView(popUp, mapParams);
    return true;
	}    
	
	@Override
  public boolean onTouchEvent(MotionEvent event, MapView mapView) {
    if (MotionEvent.ACTION_DOWN == event.getAction()) {
      // Double click handler
      if ((System.currentTimeMillis() - lastClickTime) < 500) {
        mapView.getController().zoomIn();
      }
      lastClickTime = System.currentTimeMillis();
    }
    if(mapView==null) Log.d("MapController", "MapView is null");
    return super.onTouchEvent(event, mapView);
  }

  public void addOverlay(TLEventMarker overlay) {
    markers.add(overlay);
    populate();
  }

  public Drawable boundDrawable(Drawable drawable) {
    return boundCenterBottom(drawable);
  }
}