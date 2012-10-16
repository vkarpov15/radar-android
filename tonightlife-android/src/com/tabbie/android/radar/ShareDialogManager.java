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
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.DialogInterface.OnShowListener;
import android.text.Editable;
import android.text.TextWatcher;
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

	// Dialogs used by this manager
	public Dialog friendsDialog;
	public Dialog messageDialog;
	
	// Internal Variables
	private String mEventId;
	private TextView vTooLong;
	private EditText vMessageText;
	private boolean tooLong = false;
	
	public ShareDialogManager(Context context) {
		this.mContext = context;
		this.mSender = (ShareMessageSender) context;
		this.mBuilder = new AlertDialog.Builder(context);
		this.mIds = new LinkedHashSet<String>();
		
		// Get resources
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
		
		/*
		 * Can we build it?
		 * YES WE CAN!
		 * 
		 * This needs to be final so we can
		 * set our own listener in setOnShowListener
		 * with a self reference
		 */
		final AlertDialog d = mBuilder.setTitle(mFriendsListTitle)
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
		
		/*
		 * We pass null here because we're going to
		 * SET OUR OWN FUCKING LISTENER!!!
		 * (See setOnShowListener later)
		 */
		.setPositiveButton(mOkayButton, null)
		.create();
		
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
	
	/**
	 * Dialog for writing a message,
	 * displayed once a friend/friends
	 * has/have been chosen
	 */
	private void displayMessageDialog() {
		View content = LayoutInflater.from(mContext).inflate(R.layout.share_message, null);
		
		vTooLong = (TextView) content.findViewById(R.id.share_message_notification);
		vMessageText = (EditText) content.findViewById(R.id.share_message_editable);
		vMessageText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().length() > 140) {
					tooLong = true;
					vTooLong.setVisibility(View.VISIBLE);
				} else {
					tooLong = false;
					vTooLong.setVisibility(View.GONE);
				}
			}
		});
		
		final AlertDialog d = new AlertDialog.Builder(mContext)
		.setTitle(mMessageTitle)
		.setPositiveButton(mSendButton, null)
		.setView(content)
		.create();
		
		d.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(!tooLong && (vMessageText.getText().length() > 0)) {
							mSender.sendMessage(mIds, vMessageText.getText().toString(), mEventId);
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
	
	/**
	 * Make sure to call this
	 * before sending a new message
	 * 
	 * @param eventId The event the user clicked
	 */
	public void setEventId(String eventId) {
		this.mEventId = eventId;
	}
	
	public interface ShareMessageSender {
		abstract void sendMessage(Set<String> ids, String message, String eventId);
	}
}