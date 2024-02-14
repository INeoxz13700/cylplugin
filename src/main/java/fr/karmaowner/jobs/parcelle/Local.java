package fr.karmaowner.jobs.parcelle;

import fr.karmaowner.common.Main;
import fr.karmaowner.utils.ItemUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Local {
  public static final String NAME = "Local";
  
  private static Inventory inv = Main.INSTANCE.getServer().createInventory(null, 18, "Local");
  
  public static HashMap<ItemStack, Parcelle> regions = new HashMap<>();
  
  public static void fillInventory() {
    inv.clear();
    for (ItemStack item : regions.keySet()) {
      if(item == null) continue;

      if(item.getItemMeta() == null) continue;

      if(item.getType() == null) continue;

      if(item.getData() == null) continue;

      inv.addItem(item);

    } 
  }
  
  public static boolean isRegionExist(String name) {
    for (Parcelle rg : regions.values()) {
      if (rg.getName().equals(name))
        return true; 
    } 
    return false;
  }
  
  public static boolean isRegionExistByItemName(String displayName) {
    for (ItemStack item : regions.keySet()) {
      if (item.getItemMeta().getDisplayName().equals(displayName))
        return true; 
    } 
    return false;
  }
  
  public static Parcelle getRegion(ItemStack item) {
    for (Map.Entry<ItemStack, Parcelle> rg : regions.entrySet()) {
      if (((ItemStack)rg.getKey()).getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
        return rg.getValue(); 
    } 
    return null;
  }
  
  public static Inventory getInv() {
    return inv;
  }
  
  public static void loadData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String name = "Parcelle.Local";
    ConfigurationSection section = f.getConfigurationSection(name);
    if (section != null) {
      for (String key : section.getKeys(false)) {
        int id = f.getInt(name + "." + key + ".id");
        byte data = Byte.parseByte(f.getString(name + "." + key + ".byte"));
        double price = f.getDouble(name + "." + key + ".price");
        String displayName = f.getString(name + "." + key + ".displayName");
        List<String> lore = new ArrayList<>();
        lore.add("§cPrix: §4" + price + "€");
        lore.add("§aClique droit pour selectionner");
        Parcelle p = new Parcelle(key);
        p.setPrice(price);
        regions.put(ItemUtils.getItem(id, data, 1, displayName, lore), p);
      } 
      fillInventory();
    } 
  }
}
