package com.tabbie.android.radar.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class AbstractViewInflater<T> {
	private final Context mContext;
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

	protected abstract View bindView(T data, View v);
}
