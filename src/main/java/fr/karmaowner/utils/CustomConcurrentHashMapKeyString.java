package fr.karmaowner.utils;

public class CustomConcurrentHashMapKeyString<V> extends CustomConcurrentHashMap<String, V> {
  public boolean containsKey(String key) {
    return containsKey(key.toLowerCase());
  }
  
  public V get(String key) {
    return (key != null) ? get(key.toLowerCase()) : null;
  }
  
  public V put(String key, V value) {
    return (key != null) ? super.put(key.toLowerCase(), value) : null;
  }
}
