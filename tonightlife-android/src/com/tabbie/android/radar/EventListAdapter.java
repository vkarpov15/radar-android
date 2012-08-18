package com.tabbie.android.radar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {
	
	private final Context context;
	private final EventClickListener eventClickListener;
	private final EventLocationClickListener eventLocationClickListener;
	
	private final List<Event> events;
	private List<Event> filteredEvents;
	
	
	public ImageLoader imageLoader; // I don't know why this is public, but it is in the source

    public EventListAdapter(Context context, List<Event> events) {
    	
    	this.events = events;
    	this.filteredEvents = new ArrayList<Event>(this.events);
    	
    	this.context = context;
    	this.eventClickListener = (EventClickListener) context;
    	this.eventLocationClickListener = (EventLocationClickListener) context;
    	imageLoader = new ImageLoader(context);
    }

    @Override
    public View getView(final int position,
    		View convertView, final ViewGroup parent) {
    	
      if (null == convertView) {
    	Log.i("EventListAdapter", "Instantiating a new list view element");
        convertView = LayoutInflater.from(context).inflate(R.layout.event_list_element, null);
      }
      
      final Event e = (Event) getItem(position);
      
      
      // Populate TextViews with data
      ((TextView) convertView
    		  .findViewById(R.id.event_text))
    		  .setText(e.name);

      ((TextView) convertView
    		  .findViewById(R.id.event_list_time))
    		  .setText(e.time.makeYourTime());

      ((TextView) convertView
          .findViewById(R.id.event_location))
          .setText(e.venueName);

      ((TextView) convertView
    		  .findViewById(R.id.upvotes))
    		  .setText(Integer.toString(e.radarCount));
      
      final View viewHolder = convertView.findViewById(R.id.image_holder);
      viewHolder.findViewById(R.id.element_loader).startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));
      imageLoader.displayImage(e.image.toString(),
    		  (ImageView) viewHolder.findViewById(R.id.event_image));
      
      convertView.findViewById(R.id.list_list_element_layout).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			eventClickListener.onEventClicked(e, position, imageLoader.getBitmap(e.image.toString()));
		}
      });
      
      convertView.findViewById(R.id.location_image_layout).setOnClickListener(
          new OnClickListener() {
            public void onClick(View v) {
            	eventLocationClickListener.onEventLocationClicked(e);
            }
          });
      
      return convertView;
    }

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static void filterByAge(final List<Event> eventList,
			final short ageReq) {
		for(final Event e : eventList) {
			// TODO Remove all events that don't satisfy the age requirement
		}
	}
	
	public interface EventClickListener { // TODO Replace these with setOnBlahClickListeners
		public void onEventClicked(final Event e, final int position, final Bitmap image);
	}
	
	public interface EventLocationClickListener { // TODO Remove this functionality, replace with ListView.setOnItemClickListener();
		public void onEventLocationClicked(final Event e);
	}
  }