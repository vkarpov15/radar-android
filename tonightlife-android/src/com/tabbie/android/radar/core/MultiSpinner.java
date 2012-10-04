package com.tabbie.android.radar.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 *  MultiSpinner.java
 *
 *  Created on: August 16, 2012
 *      Author: Justin Knutson
 *      
 *  An extension of Spinner that allows the user to select multiple items
 */

public class MultiSpinner extends Spinner implements
OnMultiChoiceClickListener, android.content.DialogInterface.OnCancelListener {

	private String[] items;
	private boolean[] selected;
	private String defaultText;
	private MultiSpinnerListener listener;
	
	public MultiSpinner(Context context) {
		super(context);
	}
	
	public MultiSpinner(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	public MultiSpinner(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked)
		    selected[which] = true;
		else
		    selected[which] = false;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		// refresh text on spinner
		StringBuffer spinnerBuffer = new StringBuffer();
		boolean someUnselected = false;
		for (int i = 0; i < items.length; i++) {
		    if (selected[i] == true) {
		        spinnerBuffer.append(items[i]);
		        spinnerBuffer.append(", ");
		    } else {
		        someUnselected = true;
		    }
		}
		String spinnerText;
		if (someUnselected) {
		    spinnerText = spinnerBuffer.toString();
		    if (spinnerText.length() > 2)
		        spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
		} else {
		    spinnerText = defaultText;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
		        android.R.layout.simple_spinner_item,
		        new String[] { spinnerText });
		setAdapter(adapter);
		listener.onItemsSelected(selected);
	}
	
	@Override
	public boolean performClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMultiChoiceItems(items, selected, this);
		builder.setPositiveButton(android.R.string.ok,
		        new DialogInterface.OnClickListener() {
		
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.cancel();
		            }
		        });
		builder.setOnCancelListener(this);
		builder.show();
		return true;
	}
	
	public void setMultiSpinnerListener(final MultiSpinnerListener listener) {
		this.listener = listener;
	}
	
	public void setItems(String[] items, String allText,
	    MultiSpinnerListener listener) {
		
		this.items = items;
		this.defaultText = allText;
		this.listener = listener;
		
		// all selected by default
		selected = new boolean[items.length];
		for (int i = 0; i < selected.length; i++)
		    selected[i] = true;
		
		// all text on the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
		        android.R.layout.simple_spinner_item, new String[] { allText });
		setAdapter(adapter);
		}
		
		public interface MultiSpinnerListener {
		public void onItemsSelected(boolean[] selected);
	}
}