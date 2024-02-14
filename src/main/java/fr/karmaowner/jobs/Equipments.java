package fr.karmaowner.jobs;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class Equipments {
  private ArrayList<ItemStack> equipments = new ArrayList<>();
  
  public void addEquipment(int id) {
    this.equipments.add(new ItemStack(id));
  }
  
  public void addEquipment(int id, byte data) {
    ItemStack item = new ItemStack(id, 1, (short)0, data);
    this.equipments.add(item);
  }
  
  public ArrayList<ItemStack> getEquipments() {
    return this.equipments;
  }
}
