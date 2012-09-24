package com.tabbie.android.radar.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tabbie.android.radar.model.Event;

public abstract class AbstractEventListAdapter<T extends Event> extends BaseAdapter {
	private final int mResource;
	private final Context mContext;
	private final List<T> mEventList;
	
	public AbstractEventListAdapter(Context context, List<T> listManager, int resource) {
		this.mResource = resource;
		this.mContext = context;
		this.mEventList = listManager;
	}

	@Override
	public int getCount() {
		return mEventList.size();
	}

	@Override
	public Object getItem(int position) {
		return mEventList.get(position);
	}

	@Override
	public long getItemId(int position) {
		final T item = mEventList.get(position);
		try {
			final long id = Long.valueOf(item.id);
			return id;
		} catch(NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if(v == null) {
			v = LayoutInflater.from(mContext).inflate(mResource, null);
			v.setClickable(false);
		}
		buildView(mEventList.get(position), v);
		parent.addView(v);
		return v;
	}
	
	/**
	 * Implement this method to bind
	 * data from source to the View v
	 * 
	 * @param source The Event that is
	 * being displayed to the user
	 * @param v The View the adapter will
	 * pass to its parent ListView
	 */
	public abstract void buildView(T source, View v);
}