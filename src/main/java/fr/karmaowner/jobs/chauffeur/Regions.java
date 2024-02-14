package fr.karmaowner.jobs.chauffeur;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.FileUtils;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Regions {
  public static ArrayList<Regions> listRegions = new ArrayList<>();
  
  public static FileUtils taxi = new FileUtils("Taxi", "");
  
  private ProtectedRegion rg;
  
  private ItemStack item;
  
  private String DisplayName;
  
  private static Inventory chooseDestination = Main.INSTANCE.getServer().createInventory(null, 54, ChatColor.RED + "Destination");
  
  public Regions(ProtectedRegion rg, ItemStack item, String displayname) {
    this.rg = rg;
    this.item = item;
    this.DisplayName = displayname;
  }
  
  public static void fillDestinationInventory() {
    chooseDestination.clear();
    for (Regions r : listRegions) {
      chooseDestination.addItem(r.getItem());
    } 
  }
  
  public static void setRegion(String region, String displayName, int id, byte data, Player p) {
    ProtectedRegion r = (ProtectedRegion)Main.WG.getRegionManager(Main.INSTANCE.getServer().getWorld("cyl")).getRegions().get(region);
    if (r != null) {
      ItemStack item = new ItemStack(id, 1, (short)0, data);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(displayName);
      ArrayList<String> l = new ArrayList<>();
      l.add(ChatColor.GREEN + "Prix par m: " + 0.2D);
      meta.setLore(l);
      item.setItemMeta(meta);
      listRegions.add(new Regions(r, item, displayName));
      fillDestinationInventory();
      p.sendMessage(ChatColor.GREEN + "Région ajoutée !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette région n'existe pas !");
    } 
  }
  
  public static void setRegion(String region, String displayName, int id, byte data) {
    ProtectedRegion r = (ProtectedRegion)Main.WG.getRegionManager(Main.INSTANCE.getServer().getWorld("cyl")).getRegions().get(region);
    if (r != null) {
      ItemStack item = new ItemStack(id, 1, (short)0, data);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(displayName);
      ArrayList<String> l = new ArrayList<>();
      l.add(ChatColor.GREEN + "Prix par m: " + 0.2D);
      meta.setLore(l);
      item.setItemMeta(meta);
      listRegions.add(new Regions(r, item, displayName));
      fillDestinationInventory();
    } 
  }
  
  public static Inventory getInventoryDestination() {
    return chooseDestination;
  }
  
  public static Regions getRegion(String region) {
    for (Regions r : listRegions) {
      if (r.rg.getId().equals(region))
        return r; 
    } 
    return null;
  }
  
  public static ProtectedRegion getRegionByItem(ItemStack item) {
    for (Regions r : listRegions) {
      if (r.item.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
        return r.rg; 
    } 
    return null;
  }
  
  public static void removeRegion(String name, Player p) {
    Regions r = getRegion(name);
    if (r != null) {
      listRegions.remove(r);
      p.sendMessage(ChatColor.GREEN + "Région supprimée !");
      return;
    } 
    p.sendMessage(ChatColor.RED + "Aucune égion de ce nom n'a été trouvé !");
  }
  
  public static void loadData() {
    if (!taxi.directoryExist())
      taxi.createFile(); 
    String key = "TaxiRegions";
    taxi.loadFileConfiguration();
    if (taxi.getFileConfiguration() != null) {
      if (taxi.getFileConfiguration().get(key) != null)
        for (String key1 : taxi.getFileConfiguration().getConfigurationSection(key).getKeys(false)) {
          String region = taxi.getFileConfiguration().getString(key + "." + key1 + ".rgName");
          String displayName = taxi.getFileConfiguration().getString(key + "." + key1 + ".item.displayname");
          int id = taxi.getFileConfiguration().getInt(key + "." + key1 + ".item.id");
          byte data = Byte.parseByte(taxi.getFileConfiguration().getString(key + "." + key1 + ".item.data"));
          setRegion(region, displayName, id, data);
        }  
      taxi.getFileConfiguration().set(key, null);
    } 
  }
  
  public static void saveData() {
    Main.Log("Taxi Regions Data saving...");
    if (listRegions.isEmpty())
      return; 
    String key = "TaxiRegions";
    int i = 0;
    for (Regions r : listRegions) {
      if (r.rg != null && r.rg.getId() != null)
        taxi.getFileConfiguration().set(key + "." + i + ".rgName", r.rg.getId()); 
      if (r.DisplayName != null)
        taxi.getFileConfiguration().set(key + "." + i + ".item.displayname", r.DisplayName); 
      if (r.item != null) {
        taxi.getFileConfiguration().set(key + "." + i + ".item.id", r.item.getTypeId());
        if (r.getItem().getData() != null)
          taxi.getFileConfiguration().set(key + "." + i + ".item.data", r.item.getData().getData());
      } 
      i++;
    } 
    taxi.saveConfig();
    Main.Log("Taxi Regions Data saved");
  }
  
  public ItemStack getItem() {
    return this.item;
  }
  
  public ProtectedRegion getRg() {
    return this.rg;
  }
}
