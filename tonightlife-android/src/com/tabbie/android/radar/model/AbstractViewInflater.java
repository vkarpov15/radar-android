package com.tabbie.android.radar.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class AbstractViewInflater<T> {
	protected final Context mContext;
	private final int mResource;
	
	public AbstractViewInflater(Context context, int resource) {
		this.mContext = context;
		this.mResource = resource;
	}
	
	public View getView(T data, View v) {
		if(v == null) {
			v = LayoutInflater.from(mContext).inflate(mResource, null);
			v.setClickable(false);
		}
		return bindView(data, v);
	}

	/**
	 * Override this method to bind the data
	 * stored in T to the View v
	 * 
	 * @param data The data to populate
	 * @param v The View to populate
	 * @return The populated View
	 */
	protected abstract View bindView(T data, View v);
}