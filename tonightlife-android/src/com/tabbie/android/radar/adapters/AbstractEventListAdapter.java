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
	private final List<T> mListManager;
	
	public AbstractEventListAdapter(Context context, List<T> listManager, int resource) {
		this.mResource = resource;
		this.mContext = context;
		this.mListManager = listManager;
	}

	@Override
	public int getCount() {
		return mListManager.size();
	}

	@Override
	public Object getItem(int position) {
		return mListManager.get(position);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		if(v == null) {
			v = LayoutInflater.from(mContext).inflate(mResource, null);
			v.setClickable(false);
		}
		buildView(mListManager.get(position));
		parent.addView(v);
		return v;
	}
	
	public abstract void buildView(T source);
}