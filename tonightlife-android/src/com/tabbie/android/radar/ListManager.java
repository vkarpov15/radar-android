package com.tabbie.android.radar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import android.util.SparseArray;

public class ListManager extends ArrayList<Event> {
	private static final long serialVersionUID = -3866764055354721145L;
	
	public SparseArray<ListMember> lists;
	
	public ListManager() {
		super();
	}
	
	private ListManager(final SparseArray<ListMember> lists) {
		this.lists = lists;
	}
	
	@Override
	public boolean add(final Event e) {
		final int length = lists.size();
		for(int i = 0; i < length; i++) {
			final ListMember l = lists.valueAt(i);
			if(l.conditionator.conditionate(e)) {
				l.add(e);
			}
		}
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends Event> collection) {
		final int length = lists.size();
		for(final Event e : collection) {
			for(int i = 0; i < length; i++) {
				final ListMember l = lists.valueAt(i);
				if(l.conditionator.conditionate(e)) {
					l.add(e);
				}
			}
		}
		return super.addAll(collection);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Event> collection) {
		addAll(collection);
		return super.addAll(index, collection);
	}
	
	private static final class ListMember{
		private final ArrayList<Event> events = new ArrayList<Event>();
		protected final Conditionator conditionator;
		protected final Comparator<Event> comparator;
		
		public ListMember(final Conditionator conditionator, final Comparator<Event> comparator) {
			this.conditionator = conditionator;
			this.comparator = comparator;
		}
		
		public void add(final Event e) {
			events.add(e);
		}
	}
	
	public static final class Builder {
		private SparseArray<ListMember> members = new SparseArray<ListMember>();
		
		public Builder addList(final short key, final Conditionator conditionator) {
			members.put(key, new ListMember(conditionator, null));
			return this;
		}
		
		public Builder addList(final short key, final Conditionator conditionator, final Comparator<Event> comparator) {
			members.put(key, new ListMember(conditionator, comparator));
			return this;
		}
		
		public ListManager create() {
			return new ListManager(members);
		}
	}
	
	public interface Conditionator {
		public abstract boolean conditionate(final Event e);
	}
}