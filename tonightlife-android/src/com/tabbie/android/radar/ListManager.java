package com.tabbie.android.radar;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class ListManager implements Parcelable {
	private final ArrayList<Event> events;
	
	public ListManager() {
		events = new ArrayList<Event>();
	}
	
	public ListManager(ArrayList<Event> c) {
		events = c;
	}
	
	public void addEvent(final Event e) {
		events.add(e);
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(events);
	}
	
  public static final Parcelable.Creator<ListManager> CREATOR
		  = new Parcelable.Creator<ListManager>() {
		public ListManager createFromParcel(Parcel in) {
			final ArrayList<Event> events = new ArrayList<Event>();
			in.readTypedList(events, Event.CREATOR);
		  return new ListManager(events);
		}
		
		public ListManager[] newArray(int size) {
		  return new ListManager[size];
		}
	};
}