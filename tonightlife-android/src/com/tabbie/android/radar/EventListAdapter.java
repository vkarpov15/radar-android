package com.tabbie.android.radar;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {
	
	private final Context context;
	protected final List<Event> events;
	
	
	public ImageLoader imageLoader; // I don't know why this is public, but it is in the source

    public EventListAdapter(Context context, List<Event> events) {
    	
    	this.events = events;
    	this.context = context;
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
    		  .setText(e.getName());

      ((TextView) convertView
    		  .findViewById(R.id.event_list_time))
    		  .setText(e.getTime().makeYourTime());

      ((TextView) convertView
          .findViewById(R.id.event_location))
          .setText(e.getVenueName());
      
      final View viewHolder = convertView.findViewById(R.id.image_holder);
      viewHolder.findViewById(R.id.element_loader).startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));
      imageLoader.displayImage(e.getUrl().toString(),
    		  (ImageView) viewHolder.findViewById(R.id.event_image));
      
      
      /* If this view is clickable then
       * OnItemClickListener will NEVER
       * be called for this view. Do not
       * delete lightheartedly.
       */
      convertView.setClickable(false);
      
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
  }