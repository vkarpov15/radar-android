package com.tabbie.android.radar;

/**
 *  ShareDialogManager.java
 *
 *  Created on: October 13, 2012
 *      Author: Justin Knutson
 *      
 *  Wrapper for a ShareDialogManager that allows
 *  the user to send ShareMessages to the server
 */

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import com.tabbie.android.radar.core.facebook.FBPerson;

public class ShareDialogManager {
	private final Context mContext;
	private final AlertDialog.Builder mBuilder;
	
	public ShareDialogManager(Context context) {
		this.mContext = context;
		this.mBuilder = new AlertDialog.Builder(context);
	}
	
	public Dialog getDialog(ArrayList<FBPerson> data) {

		int length = data.size();
		CharSequence[] adapterIds = new String[length];
		for(int i = 0; i < length; i++) {
			adapterIds[i] = data.get(i).name;
		}
		
		mBuilder.setTitle("Share with...")
				.setMultiChoiceItems(adapterIds, new boolean[length], new OnMultiChoiceClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						// TODO Auto-generated method stub
						
					}
				})
				.setCancelable(true)
				.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		return mBuilder.create();
	}

}
