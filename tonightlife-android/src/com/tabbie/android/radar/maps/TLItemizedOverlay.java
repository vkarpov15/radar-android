package com.tabbie.android.radar.maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.tabbie.android.radar.Event;

public final class TLItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	public static final String TAG = "TLItemizedOverlay";
	
	private final ArrayList<OverlayItem> markers = new ArrayList<OverlayItem>();
  private long lastClickTime = -1;
  private OnTapListener listener;

	public TLItemizedOverlay(final Context context) {
		super(null);
	}

  @Override
  protected OverlayItem createItem(int i) {
    return markers.get(i);
  }

  @Override
  public int size() {
    return markers.size();
  }
	
	@Override
	protected boolean onTap(int index) {
		if(listener!=null) {
			listener.onTap(index);
			return true;
		} else {
			return false;
		}
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
    return false;
  }
	
	public void setOnTapListener(final OnTapListener listener) {
		this.listener = listener;
	}

  public void addEventMarker(Event e, Drawable markerImg) {
  	OverlayItem marker = new OverlayItem(e.location, e.name, e.description);
    markerImg.setBounds(0, 0, markerImg.getIntrinsicWidth(),
        markerImg.getIntrinsicHeight());
    marker.setMarker(boundDrawable(markerImg));
    addOverlay(marker);
  }

  private void addOverlay(OverlayItem overlay) {
    markers.add(overlay);
    populate();
  }

  private Drawable boundDrawable(Drawable drawable) {
    return boundCenterBottom(drawable);
  }
  
  protected interface OnTapListener {
  	abstract void onTap(final int index);
  }
}