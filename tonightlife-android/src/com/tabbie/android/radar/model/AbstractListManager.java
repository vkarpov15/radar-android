package com.tabbie.android.radar.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tabbie.android.radar.core.Filter;

public class AbstractListManager<T> {
  private final List<T> master = new ArrayList<T>();
  private final Map<String, OrderedSubList> lists = new LinkedHashMap<String, OrderedSubList>();
  
  private final class OrderedSubList {
    public final List<T> myList = new ArrayList<T>();
    public final Filter<T> filter;
    public final Comparator<T> ordering;
    
    public OrderedSubList(Filter<T> filter, Comparator<T> ordering) {
      this.filter = filter;
      this.ordering = ordering;
    }
  }
  
  public void createList(String name, Filter<T> filter, Comparator<T> ordering) {
    lists.put(name, new OrderedSubList(filter, ordering));
  }
  
  public void add(T obj) {
    master.add(obj);
    for (String key : lists.keySet()) {
      OrderedSubList ls = lists.get(key);
      if (ls.filter.apply(obj)) {
        int index = 0;
        // Insertion sort on elements
        for (T el : ls.myList) {
          if (ls.ordering.compare(obj, el) <= 0) {
            ls.myList.add(index, obj);
            break;
          }
          ++index;
        }
      }
    }
  }
  
  public boolean addToList(String listName, T obj) {
    OrderedSubList ls = lists.get(listName);
    if (null == ls) {
      return false;
    }
    if (ls.filter.apply(obj)) {
      int index = 0;
      // Insertion sort on elements
      for (T el : ls.myList) {
        if (ls.ordering.compare(obj, el) <= 0) {
          ls.myList.add(index, obj);
          break;
        }
        ++index;
      }
      if (ls.myList.size() == index) {
        ls.myList.add(obj);
      }
      return true;
    } else {
      return false;
    }
  }
  
  
  
}
