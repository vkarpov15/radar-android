package com.tabbie.android.radar;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class RadarActivity extends MapActivity implements OnTabChangeListener {

  private static final String LIST_FEATURED_TAG = "Featured";
  private static final String EVENT_TAB_TAG = "Events";
  private static final String RADAR_TAB_TAG = "Radar";

  private TabHost tabHost;
  private ListView featuredListView;
  private ListView allListView;
  private ListView radarListView;
  private RadarCommonController commonController;
  
  //private MapView mapView;
  //private RadarMapController mapController;
  //private MyLocationOverlay myLocationOverlay;

  private Event selected = null;

  private class EventListAdapter extends ArrayAdapter<Event> {
    private List<Event> events;

    public EventListAdapter(Context context, int resource,
        int textViewResourceId, List<Event> events) {
      super(context, resource, textViewResourceId, events);
      this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (null == convertView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.event_list_element, null);
      }
      final Event e = getItem(position);
      TextView title = (TextView) convertView.findViewById(R.id.event_text);
      title.setText(e.name);
      
      ((TextView) convertView.findViewById(R.id.event_list_time)).setText(e.time);
      ((TextView) convertView.findViewById(R.id.event_location)).setText(e.venueName);
      final TextView upVotes = ((TextView) convertView.findViewById(R.id.upvotes));
      upVotes.setText(Integer.toString(e.radarCount));
      
      ImageView img = (ImageView) convertView.findViewById(R.id.event_image);
      try {
        img.setImageDrawable(Drawable.createFromStream(e.image.openStream(),
            "src"));
      } catch (IOException exception) {
        exception.printStackTrace();
      }
      final Button radarButton = (Button) convertView.findViewById(R.id.add_to_radar_image);
      if (e.isOnRadar()) {
        radarButton.setBackgroundResource(R.drawable.radar_button_on);
      } else {
        radarButton.setBackgroundResource(R.drawable.radar_button);
      }
      
      convertView.findViewById(R.id.list_list_element_layout).setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
	        if (null != e) {
	          Intent intent = new Intent(RadarActivity.this, EventDetailsActivity.class);
	          intent.putExtra("event", e);
	          startActivity(intent);
	        }
        }
      });
      
      convertView.findViewById(R.id.add_to_radar_image).setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (e.isOnRadar() && commonController.removeFromRadar(e)) {
            radarButton.setBackgroundResource(R.drawable.radar_button);
          } else if (!e.isOnRadar() && commonController.addToRadar(e)){
            radarButton.setBackgroundResource(R.drawable.radar_button_on);
          }
          upVotes.setText(Integer.toString(e.radarCount));
          ((EventListAdapter) radarListView.getAdapter()).notifyDataSetChanged();
          ((EventListAdapter) featuredListView.getAdapter()).notifyDataSetChanged();
          ((EventListAdapter) allListView.getAdapter()).notifyDataSetChanged();
        }
      });
      return convertView;
    }

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    featuredListView = (ListView) findViewById(R.id.featured_event_list);
    allListView = (ListView) findViewById(R.id.all_event_list);
    radarListView = (ListView) findViewById(R.id.radar_list);

    commonController = new RadarCommonController();

    tabHost = (TabHost) findViewById(android.R.id.tabhost);
    tabHost.setup();
    tabHost.setOnTabChangedListener(this);

    Filter<Event> featuredOnly = new Filter<Event>() {
      @Override
      public boolean apply(Event o) {
        return o.featured;
      }
    };

    featuredListView.setAdapter(new EventListAdapter(this,
        R.id.featured_event_list, R.layout.event_list_element, featuredOnly
            .applyToList(commonController.eventsList)));
    allListView.setAdapter(new EventListAdapter(this, R.id.all_event_list,
        R.layout.event_list_element, commonController.eventsList));
    radarListView.setAdapter(new EventListAdapter(this, R.id.radar_list,
        R.layout.event_list_element, commonController.radar));

    //List<Overlay> overlays = mapView.getOverlays();
    //overlays.clear();

    //myLocationOverlay = new MyLocationOverlay(this, mapView);
    //myLocationOverlay.enableMyLocation();

    Drawable defaultMarker = getResources().getDrawable(R.drawable.marker);
    Drawable featuredMarker = getResources()
        .getDrawable(R.drawable.ic_launcher);
    
    for (final Event e : commonController.eventsList) {
      /*mapController.addEventMarker(e, e.featured ? featuredMarker
          : defaultMarker, new EventMarker.OnClickListener() {
        public void onClick() {
          
          //mapController.setLatLon(e.lat, e.lon); mapController.setZoom(16);
          
        }*/
      //});
    }

    //overlays.add(myLocationOverlay);
    //overlays.add(mapController.getItemizedOverlay());
    //mapView.postInvalidate();

    // add views to tab host
    tabHost.addTab(tabHost.newTabSpec(LIST_FEATURED_TAG)
        .setIndicator(LIST_FEATURED_TAG)
        .setContent(new TabHost.TabContentFactory() {
          public View createTabContent(String arg0) {
            return featuredListView;
          }
        }));
    tabHost.addTab(tabHost.newTabSpec(EVENT_TAB_TAG)
        .setIndicator(EVENT_TAB_TAG)
        .setContent(new TabHost.TabContentFactory() {
          public View createTabContent(String arg0) {
            return allListView;
          }
        }));
    tabHost.addTab(tabHost.newTabSpec(RADAR_TAB_TAG).setIndicator("Radar")
        .setContent(new TabHost.TabContentFactory() {
          public View createTabContent(String arg0) {
            return radarListView;
          }
        }));

    // IMPERATIVE to keep this hack here, otherwise app fubars
    tabHost.setCurrentTab(2);
    tabHost.setCurrentTab(1);
    tabHost.setCurrentTab(0);
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

  public void onTabChanged(String tabName) {
    if (tabName.equals(EVENT_TAB_TAG)) {

    } else if (tabName.equals(LIST_FEATURED_TAG)) {

    } else if (tabName.equals(RADAR_TAB_TAG)) {

    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    /*MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);*/
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
    case R.id.zoom_to_me:
      //mapController.setLatLon(myLocationOverlay.getMyLocation());
      //mapController.setZoom(16);
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }
}
