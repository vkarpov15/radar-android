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
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tabbie.android.radar.core.facebook.FBPerson;

public class ShareDialogManager {
	private final Context mContext;
	private final ShareMessageSender mSender;
	private final AlertDialog.Builder mBuilder;
	private final Set<String> ids = new LinkedHashSet<String>();
	
	public ShareDialogManager(Context context) {
		this.mContext = context;
		this.mSender = (ShareMessageSender) context;
		this.mBuilder = new AlertDialog.Builder(context);
	}
	
	public Dialog getDialog(final ArrayList<FBPerson> data) {

		int length = data.size();
		CharSequence[] adapterIds = new String[length];
		for(int i = 0; i < length; i++) {
			adapterIds[i] = data.get(i).name;
		}
		
		mBuilder.setTitle("Share with...")
				.setMultiChoiceItems(adapterIds, new boolean[length], new OnMultiChoiceClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						if(isChecked) {
							ids.add(data.get(which).id);
						} else {
							ids.remove(data.get(which).id);
						}
					}
				})
				.setCancelable(true)
				.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ShareMessageDialog().show();
					}
				});
		return mBuilder.create();
	}
	
	private class ShareMessageDialog extends Dialog {
		TextView tooLongNotifier;
		EditText messageText;
		boolean tooLong = false;

		public ShareMessageDialog() {
			super(mContext);
		}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.share_message);
			tooLongNotifier = (TextView) findViewById(R.id.share_message_notification);
			messageText = (EditText) findViewById(R.id.share_message_editable);
			messageText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					if(s.toString().length() > 140) {
						tooLong = true;
						tooLongNotifier.setVisibility(View.VISIBLE);
					} else {
						tooLong = false;
						tooLongNotifier.setVisibility(View.GONE);
					}
				}
			});
			
			findViewById(R.id.share_message_send_button).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!tooLong) {
						Log.d("Hooray!", "It sends!");
						mSender.send(ids, messageText.getText().toString());
					}
				}
			});
		}
	}
	
	public interface ShareMessageSender {
		abstract void send(Set<String> ids, String message);
	}
}