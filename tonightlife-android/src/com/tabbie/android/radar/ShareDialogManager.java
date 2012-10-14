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
	private final Context mContext;
	private final ShareMessageSender mSender;
	private final AlertDialog.Builder mBuilder;
	private final Set<String> ids = new LinkedHashSet<String>();
	private TextView notifyTooLong;
	private EditText messageText;
	boolean tooLong = false;
	private Dialog friendsDialog, messageDialog;
	
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
						if(ids.isEmpty()) {
							Toast.makeText(mContext, "No one selected!", Toast.LENGTH_SHORT).show();
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
		messageText.setText("This is a test of the 144 character limiting system that Justin Knutson has put in place to make sure bitches don't overflow this dialogue");
		
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
		.setTitle("Message")
		.setPositiveButton("Send", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!tooLong) {
					Log.d("Hooray!", "It sends!");
					mSender.send(ids, messageText.getText().toString());
				}
			}
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
							Log.d("Hooray!", "It sends!");
							mSender.send(ids, messageText.getText().toString());
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
	
	public interface ShareMessageSender {
		abstract void send(Set<String> ids, String message);
	}
}