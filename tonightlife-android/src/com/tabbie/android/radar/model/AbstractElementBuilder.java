package com.tabbie.android.radar.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class AbstractElementBuilder<T> {
	protected final Context mContext;
	private final int mResource;
	
	public AbstractElementBuilder(Context context, int resource) {
		this.mContext = context;
		this.mResource = resource;
	}
	
	/**
	 * Override this method to customize
	 * the view based on the data backing
	 * this AbstractElementBuilder
	 * 
	 * @param data The object with data
	 * to populate the view
	 * 
	 * @param v The view to be populated
	 * 
	 * @return The populated view
	 */
	public View buildView(T data, View v) {
		if(v == null) {
			v = LayoutInflater.from(mContext).inflate(mResource, null);
			v.setClickable(false);
		}
		return v;
	}
}