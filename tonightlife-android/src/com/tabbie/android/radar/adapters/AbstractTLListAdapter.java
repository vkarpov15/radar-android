package com.tabbie.android.radar.adapters;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractTLListAdapter<T> extends BaseAdapter {
	private final List<T> mListManager;
	
	public AbstractTLListAdapter(final List<T> listManager) {
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
		v = buildView(mListManager.get(position));
		parent.addView(v);
		return v;
	}
	
	public abstract View buildView(final T source);
}