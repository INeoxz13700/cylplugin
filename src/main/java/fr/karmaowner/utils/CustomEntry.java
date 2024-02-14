package fr.karmaowner.utils;

public class CustomEntry<K, V> {
  private K key;
  
  private V value;
  
  public CustomEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }
  
  public K getKey() {
    return this.key;
  }
  
  public V getValue() {
    return this.value;
  }
}
