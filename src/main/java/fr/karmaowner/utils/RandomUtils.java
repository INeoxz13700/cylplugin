package fr.karmaowner.utils;

import java.util.ArrayList;
import java.util.List;

public class RandomUtils<T> {
  private ArrayList<T> objects = new ArrayList<>();
  
  public void addObj(T obj) {
    this.objects.add(obj);
  }
  
  public void ExtractItemToList(List<T> obj) {
    while (!this.objects.isEmpty()) {
      T obj1 = getExtractObj();
      obj.add(obj1);
    } 
  }
  
  public ArrayList<T> getObjects() {
    return this.objects;
  }
  
  public T getObj() {
    int index = (int)(Math.random() * this.objects.size());
    return this.objects.get(index);
  }
  
  public T getExtractObj() {
    int index = (int)(Math.random() * this.objects.size());
    T item = this.objects.get(index);
    this.objects.remove(index);
    return item;
  }
}
