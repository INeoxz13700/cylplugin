package fr.karmaowner.utils;

import java.util.concurrent.ConcurrentHashMap;

public class CustomConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
  public boolean containsKey(Object key) {
    if (key != null)
      return super.containsKey(key); 
    return false;
  }
  
  public boolean containsValue(Object value) {
    if (value != null)
      return super.containsValue(value); 
    return false;
  }
  
  public V get(Object key) {
    if (key != null)
      return super.get(key); 
    return null;
  }
  
  public boolean contains(Object value) {
    if (value != null)
      return super.contains(value); 
    return false;
  }
  
  public V put(K key, V value) {
    if (key != null && value != null)
      return super.put(key, value); 
    return null;
  }
}
