package fr.karmaowner.utils;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
  public static ItemStack fromId(String id, List<String> description, String name, int quantity) {
    int itemId;
    byte material = 0;
    if (id.contains(":")) {
      String[] ids = id.split(":");
      itemId = Integer.parseInt(ids[0]);
      material = (byte)Integer.parseInt(ids[1]);
    } else {
      itemId = Integer.parseInt(id);
    } 
    if (description != null || name != null) {
      ItemStack it = new ItemStack(Material.getMaterial(itemId), quantity, (short)material);
      ItemMeta itMeta = it.getItemMeta();
      if (description != null)
        itMeta.setLore(description); 
      if (name != null)
        itMeta.setDisplayName(name); 
      return it;
    } 
    return new ItemStack(Material.getMaterial(itemId), quantity, (short)material);
  }
  
  public static void addItem(ItemStack item, Player p) {
    PlayerInventory pInventory = p.getInventory();
    pInventory.addItem(item);
  }
  
  public static boolean addItemToInventorySafe(ItemStack is, Player p) {
    PlayerInventory pInventory = p.getInventory();
    for (int i = 0; i < pInventory.getStorageContents().length; i++) {
      ItemStack inventoryIs = pInventory.getStorageContents()[i];
      if (inventoryIs == null) {
        pInventory.setItem(i, is);
        is.setAmount(0);
        return true;
      } 
      if (isSameTypeAndCanMerge(is, inventoryIs))
        if (mergeItemStack1ToItemStack2(is, inventoryIs))
          return true;  
    } 
    return false;
  }
  
  public static boolean isSameTypeAndCanMerge(ItemStack is1, ItemStack is2) {
    return (is1.getTypeId() == is2.getTypeId() && is2.getAmount() < is2.getMaxStackSize());
  }
  
  public static boolean IsEquipable(ItemStack i) {
    net.minecraft.server.v1_12_R1.ItemStack r = CraftItemStack.asNMSCopy(i);
    return (r != null && r.getItem() != null && r.getItem() instanceof net.minecraft.server.v1_12_R1.ItemArmor);
  }
  
  public static boolean mergeItemStack1ToItemStack2(ItemStack is1, ItemStack is2) {
    if (is2.getAmount() + is1.getAmount() <= is2.getMaxStackSize()) {
      is2.setAmount(is2.getAmount() + is1.getAmount());
      is1.setAmount(0);
      return true;
    } 
    int canMergeAmount = is2.getMaxStackSize() - is2.getAmount();
    int mergedAmount = is1.getAmount() - (is1.getAmount() - canMergeAmount);
    is2.setAmount(is2.getAmount() + mergedAmount);
    is1.setAmount(is1.getAmount() - mergedAmount);
    return false;
  }
  
  public static boolean removeItemFromInventory(int id, byte type, int ammount, Player p) {
    PlayerInventory playerInventory = p.getInventory();
    for (int i = 0; i < playerInventory.getSize(); i++) {
      ItemStack it = playerInventory.getItem(i);
      if (it != null && it.getTypeId() == id && it.getData().getData() == type && it.getAmount() >= ammount) {
        if (it.getAmount() - ammount > 1) {
          it.setAmount(it.getAmount() - ammount);
        } else {
          playerInventory.remove(it);
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public static ItemStack getItem(int id, byte data, int amount, String displayName, List<String> lores) {
    ItemStack item = new ItemStack(id, amount, data);
    ItemMeta i = item.getItemMeta();
    if (i != null) {
      i.setDisplayName(displayName);
      i.setLore(lores);
      item.setItemMeta(i);
    }
    return item;
  }
  
  public static boolean compareById(ItemStack item1, ItemStack item2) {
    return (item1.getTypeId() == item2.getTypeId() && item1.getData().getData() == item2.getData().getData());
  }
  
  public static boolean compareByIdAndAmount(ItemStack item1, ItemStack item2) {
    return (compareById(item1, item2) && item2.getAmount() >= item1.getAmount());
  }
}
