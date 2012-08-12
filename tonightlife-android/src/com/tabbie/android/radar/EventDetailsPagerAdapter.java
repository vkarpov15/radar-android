package com.tabbie.android.radar;

import android.content.Context;
import android.view.View;

public class EventDetailsPagerAdapter extends android.support.v4.view.PagerAdapter {
	
	private final Context context;
	private final RadarCommonController controller;
	
	public EventDetailsPagerAdapter(final Context context,
			final RadarCommonController controller) {
		
		this.context = context;
		this.controller = controller;
	}

	@Override
	public int getCount() {
		return controller.featuredList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
