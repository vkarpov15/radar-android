package com.tabbie.android.radar;

/**
 *  ShareDialogManager.java
 *
 *  Created on: October 13, 2012
 *      @Author: Justin Knutson
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
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.DialogInterface.OnShowListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tabbie.android.radar.core.facebook.FBPerson;

public class ShareDialogManager {
	
	// Immutable private variables
	private final Context mContext;
	private final ShareMessageSender mSender;
	private final AlertDialog.Builder mBuilder;
	private final Set<String> mIds;
	
	// String resources
	private final String mFriendsListTitle;
	private final String mOkayButton;
	private final String mSendButton;
	private final String mNobodySelected;
	private final String mMessageTitle;
	
	private String mEventId;
	private TextView notifyTooLong;
	private EditText messageText;
	private boolean tooLong = false;
	private Dialog friendsDialog;
	private Dialog messageDialog;
	
	public ShareDialogManager(Context context) {
		this.mContext = context;
		this.mSender = (ShareMessageSender) context;
		this.mBuilder = new AlertDialog.Builder(context);
		this.mIds = new LinkedHashSet<String>();
		
		this.mFriendsListTitle = mContext.getString(R.string.friends_list_title);
		this.mOkayButton = mContext.getString(R.string.okay_button);
		this.mNobodySelected = mContext.getString(R.string.no_name_selected);
		this.mSendButton = mContext.getString(R.string.send_button);
		this.mMessageTitle = mContext.getString(R.string.message_title);
	}
	
	public Dialog makeDialog(final ArrayList<FBPerson> data) {

		int length = data.size();
		CharSequence[] adapterIds = new String[length];
		for(int i = 0; i < length; i++) {
			adapterIds[i] = data.get(i).name;
		}
		
		mBuilder.setTitle(mFriendsListTitle)
		.setMultiChoiceItems(adapterIds, new boolean[length], new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(isChecked) {
					mIds.add(data.get(which).id);
				} else {
					mIds.remove(data.get(which).id);
				}
			}
		})
		.setCancelable(true)
		.setPositiveButton(mOkayButton, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		
		final AlertDialog d = mBuilder.create();
		d.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mIds.isEmpty()) {
							Toast.makeText(mContext, mNobodySelected, Toast.LENGTH_SHORT).show();
						} else {
							displayMessageDialog();
						}
					}
				});
			}
		});
		friendsDialog = d;
		return d;
	}
	
	private void displayMessageDialog() {
		View content = LayoutInflater.from(mContext).inflate(R.layout.share_message, null);
		
		notifyTooLong = (TextView) content.findViewById(R.id.share_message_notification);
		messageText = (EditText) content.findViewById(R.id.share_message_editable);
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
					notifyTooLong.setVisibility(View.VISIBLE);
				} else {
					tooLong = false;
					notifyTooLong.setVisibility(View.GONE);
				}
			}
		});
		
		final AlertDialog d = new AlertDialog.Builder(mContext)
		.setTitle(mMessageTitle)
		.setPositiveButton(mSendButton, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		})
		.setView(content)
		.create();
		
		d.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(!tooLong && (messageText.getText().length() > 0)) {
							Log.d("Hooray!", "Event Id is " + mEventId);
							mSender.send(mIds, messageText.getText().toString(), mEventId);
							messageDialog.dismiss();
							friendsDialog.dismiss();
						}
					}
				});
			}
		});
		messageDialog = d;
		d.show();
	}
	
	public void setEventId(String eventId) {
		this.mEventId = eventId;
		Log.d("Set Event Id", "Event ID is " + mEventId);
	}
	
	public interface ShareMessageSender {
		abstract void send(Set<String> ids, String message, String eventId);
	}
}