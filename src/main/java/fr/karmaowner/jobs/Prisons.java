package fr.karmaowner.jobs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.ItemUtils;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Prisons {
  public static final String PRISONINVNAME = ChatColor.GOLD + "Prison durées";
  
  public static final int ITEMPRISON = 0;
  
  public static final byte DATAITEMPRISON = 4;
  
  public static int[] seconds = new int[] { 30, 60, 120, 240, 480 };
  
  public static Inventory invPrison = Main.INSTANCE.getServer().createInventory(null, 9, PRISONINVNAME);
  
  public static ArrayList<String> prisons = new ArrayList<>();
  
  public static void add(String rgName, Player p) {
    if (!isExist(rgName)) {
      ProtectedRegion rg = Main.WG.getRegionManager(p.getWorld()).getRegion(rgName);
      if (rg != null) {
        prisons.add(rgName);
        p.sendMessage(ChatColor.GREEN + "Prison ajoutée dans la liste !");
      } else {
        p.sendMessage(ChatColor.RED + "Cette region n'existe pas !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Cette prison existe déjà");
    } 
  }
  
  public static void fillInvPrison() {
    for (int s : seconds) {
      ItemStack item = ItemUtils.getItem(0, (byte)4, 1, ChatColor.RED.toString() + s + " secondes", null);
      invPrison.addItem(item);
    } 
  }
  
  public static int getSecondsByItem(ItemStack item) {
    int i = 0;
    for (ItemStack item2 : invPrison.getContents()) {
      if (item2.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
        break; 
      i++;
    } 
    return seconds[i];
  }
  
  public static void remove(String rgName, Player p) {
    if (isExist(rgName)) {
      prisons.remove(rgName);
      p.sendMessage(ChatColor.GREEN + "Prison supprimée de la liste !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette prison n'existe pas !");
    } 
  }
  
  public static ProtectedRegion getRandom() {
    int random = (int)(Math.random() * prisons.size());
    int i = 0;
    for (String name : prisons) {
      ProtectedRegion rg = Main.WG.getRegionManager(Main.INSTANCE.getServer().getWorld("cyl")).getRegion(name);
      if (i == random) {
        if (rg != null)
          return rg; 
        return null;
      } 
      i++;
    } 
    return null;
  }
  
  public static boolean isExist(String rgName) {
    for (String name : prisons) {
      if (name.equals(rgName))
        return true; 
    } 
    return false;
  }
  
  public static void loadData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String name = "Prisons";
    if (f.getStringList("Prisons") == null)
      return;
    prisons.addAll(f.getStringList("Prisons"));
  }
  
  public static void saveData() {
    Main.Log("Prisons Data saving...");
    if (prisons.isEmpty())
      return; 
    FileConfiguration f = Main.INSTANCE.getConfig();
    String name = "Prisons";
    f.set(name, prisons);
    Main.Log("Prisons Data saved");
  }
}
