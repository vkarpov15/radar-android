package com.tabbie.android.radar.model;

import android.content.Context;
import android.view.View;

public class MessageListElementBuilder<T extends ShareMessage> extends AbstractElementBuilder<T> {
	public MessageListElementBuilder(Context context, int resource) {
		super(context, resource);
	}
	
	@Override
	public View buildView(T data, View v) {
		v = super.buildView(data, v);
		// TODO Code to match the xml resource
		return v;
	}
}
