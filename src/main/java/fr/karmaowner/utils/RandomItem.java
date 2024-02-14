package fr.karmaowner.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class RandomItem {
  private ArrayList<ItemStack> items = new ArrayList<>();
  
  public void addItem(ItemStack item) {
    this.items.add(item);
  }
  
  public void ExtractItemToList(List<ItemStack> itms) {
    while (!this.items.isEmpty()) {
      ItemStack item = getExtractItem();
      itms.add(item);
    } 
  }
  
  public ArrayList<ItemStack> getItems() {
    return this.items;
  }
  
  public ItemStack getItem() {
    int index = (int)(Math.random() * this.items.size());
    return this.items.get(index);
  }
  
  public ItemStack getExtractItem() {
    int index = (int)(Math.random() * this.items.size());
    ItemStack item = this.items.get(index);
    this.items.remove(index);
    return item;
  }
}
