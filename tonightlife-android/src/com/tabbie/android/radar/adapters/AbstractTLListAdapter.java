package com.tabbie.android.radar.adapters;

import com.tabbie.android.radar.model.AbstractListManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractTLListAdapter<T> extends BaseAdapter {
	final AbstractListManager<T> mListManager;
	
	public AbstractTLListAdapter(final AbstractListManager<T> listManager) {
		this.mListManager = listManager;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public abstract View buildView(final T source);
}
