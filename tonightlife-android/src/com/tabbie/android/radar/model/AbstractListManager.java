package com.tabbie.android.radar.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tabbie.android.radar.core.AbstractFilter;

public class AbstractListManager<T> {
  public final ArrayList<T> master = new ArrayList<T>();
  
  private final Map<String, OrderedSubList> lists = new LinkedHashMap<String, OrderedSubList>();
  
  private final class OrderedSubList {
    public final ArrayList<T> myList = new ArrayList<T>();
    public final AbstractFilter<T> filter;
    
    public OrderedSubList(AbstractFilter<T> filter) {
      this.filter = filter;
    }
  }
  
  public void createList(String name, AbstractFilter<T> filter) {
    lists.put(name, new OrderedSubList(filter));
  }
  
  public void add(T obj) {
    master.add(obj);
    for (String key : lists.keySet()) {
      OrderedSubList ls = lists.get(key);
      if (ls.filter.apply(obj)) {
      	ls.myList.add(obj);
      }
    }
  }
  
  public void addAll(List<T> obj) {
  	for(T item : obj) {
  		add(item);
  	}
  }
  
  public void clear() {
  	master.clear();
  	Set<String> keySet = lists.keySet();
  	for(String key : keySet) {
  		lists.get(key).myList.clear();
  	}
  }
  
  public ArrayList<T> get(String listId) {
  	return lists.get(listId).myList;
  }
}