package com.tabbie.android.radar.maps;

import java.util.ArrayList;
import java.util.List;

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
  
  public TLItemizedOverlay(final List<Event> events, final Drawable drawable) {
  	super(null);
    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight());
  	for(final Event e : events) {
  		final OverlayItem marker = new OverlayItem(e.location, e.name, e.description);
      marker.setMarker(boundCenterBottom(drawable));
      markers.add(marker);
  	}
  	super.populate();
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
	
	public void changeDrawable(final int index, final Drawable d) {
		markers.get(index).setMarker(boundCenterBottom(d));
		super.populate();
	}
	
	public void setOnTapListener(final OnTapListener listener) {
		this.listener = listener;
	}
  
  protected interface OnTapListener {
  	abstract void onTap(final int index);
  }
}