package com.tabbie.android.radar;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class RadarActivity extends MapActivity implements OnTabChangeListener {

  private static final String LIST_FEATURED_TAG = "Featured";
  private static final String EVENT_TAB_TAG = "Events";
  private static final String RADAR_TAB_TAG = "Radar";

  private TabHost tabHost;
  private ListView featuredListView;
  private ListView allListView;
  //private MapView mapView;
  private ListView radarListView;
  private RadarCommonController commonController;
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
      ImageView img = (ImageView) convertView.findViewById(R.id.event_image);
      try {
        img.setImageDrawable(Drawable.createFromStream(e.image.openStream(),
            "src"));
      } catch (IOException exception) {
        exception.printStackTrace();
      }
      final View v = convertView.findViewById(R.id.list_list_element_layout);
      v.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
	        if (null != e) {
	          Intent intent = new Intent(RadarActivity.this, EventDetailsActivity.class);
	          intent.putExtra("event", e);
	          startActivity(intent);
	        }
		}
      });
      return convertView;
    }

  }

  private class RadarListAdapter extends ArrayAdapter<Event> {
    private List<Event> events;

    public RadarListAdapter(Context context, int resource,
        int textViewResourceId, List<Event> events) {
      super(context, resource, textViewResourceId, events);
      this.events = events;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      if (null == convertView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.event_list_element, null);
      }
      TextView title = (TextView) convertView.findViewById(R.id.event_text);
      title.setText(getItem(position).name);
      ImageView img = (ImageView) convertView.findViewById(R.id.event_image);
      try {
        img.setImageDrawable(Drawable.createFromStream(
            getItem(position).image.openStream(), "src"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      return convertView;
    }

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    //mapView = (MapView) findViewById(R.id.mapview);
    //mapView.setBuiltInZoomControls(true);

    featuredListView = (ListView) findViewById(R.id.featured_event_list);
    allListView = (ListView) findViewById(R.id.all_event_list);
    radarListView = (ListView) findViewById(R.id.radar_list);

    commonController = new RadarCommonController();
    //mapController = new RadarMapController(commonController, mapView,
    //    getResources().getDrawable(R.drawable.marker));

    tabHost = (TabHost) findViewById(android.R.id.tabhost);
    // setup must be called if you are not inflating the tabhost from XML
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
            .applyToList(commonController.getEvents())));
    allListView.setAdapter(new EventListAdapter(this, R.id.all_event_list,
        R.layout.event_list_element, commonController.getEvents()));
    radarListView.setAdapter(new RadarListAdapter(this, R.id.radar_list,
        R.layout.radar_list_element, commonController.getRadarEvents()));

    //List<Overlay> overlays = mapView.getOverlays();
    //overlays.clear();

    //myLocationOverlay = new MyLocationOverlay(this, mapView);
    //myLocationOverlay.enableMyLocation();

    List<Event> events = commonController.getEvents();

    Drawable defaultMarker = getResources().getDrawable(R.drawable.marker);
    Drawable featuredMarker = getResources()
        .getDrawable(R.drawable.ic_launcher);
    for (final Event e : events) {
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
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);
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
