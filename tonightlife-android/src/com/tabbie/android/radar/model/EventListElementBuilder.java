package com.tabbie.android.radar.model;

import com.tabbie.android.radar.ImageLoader;
import com.tabbie.android.radar.R;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListElementBuilder<T extends Event> extends AbstractElementBuilder<T> {
	private final ImageLoader mLoader;
	
	public EventListElementBuilder(Context context, int resource) {
		super(context, resource);
		this.mLoader = new ImageLoader(mContext);
	}

	@Override
	public View buildView(T data, View v) {
		v = super.buildView(data, v);
		
    ((TextView) v.findViewById(R.id.event_text))
  		  .setText(data.name);

    ((TextView) v.findViewById(R.id.event_list_time))
  		  .setText(data.time.makeYourTime());

    ((TextView) v.findViewById(R.id.event_location))
        .setText(data.venue);
    
    final View viewHolder = v.findViewById(R.id.image_holder);
    viewHolder.findViewById(R.id.element_loader).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate));
    mLoader.displayImage(data.imageUrl.toString(),
  		  (ImageView) viewHolder.findViewById(R.id.event_image));
		return v;
	}
}
