package fr.karmaowner.gangs;

import fr.karmaowner.common.Main;
import fr.karmaowner.utils.ItemUtils;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public abstract class Chest {
  public abstract List<ItemStack> getContents();
  
  public static List<ItemStack> loadChest(String typeChest) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String section = "Chests";
    String type = typeChest;
    List<ItemStack> items = new ArrayList<>();
    if (f.getConfigurationSection(section + "." + type) != null)
      for (String key : f.getConfigurationSection(section + "." + type).getKeys(false)) {
        int id = f.getInt(section + "." + type + "." + key + ".id");
        byte data = Byte.parseByte(f.getString(section + "." + type + "." + key + ".data"));
        int quantity = f.getInt(section + "." + type + "." + key + ".quantity");
        ItemStack is = ItemUtils.getItem(id, data, quantity, null, null);
        if(is.getType() == Material.AIR)
        {
          Main.Log("item with id " + id + ":" + data + " don't exist skipping.");
          continue;
        }
        items.add(ItemUtils.getItem(id, data, quantity, null, null));
      }  
    return items;
  }
}
