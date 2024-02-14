package fr.karmaowner.utils;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.PlayerData;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
  private Player p;
  
  private ItemStack item;
  
  private Inventory inv;
  
  public void setPlayer(Player p) {
    this.p = p;
  }
  
  public void setItem(ItemStack item) {
    this.item = item;
  }
  
  public void setInventory(Inventory inv) {
    this.inv = inv;
  }
  
  public boolean isOnPlayerInventory() {
    if (this.item == null)
      return false; 
    for (ListIterator<ItemStack> listIterator = this.inv.iterator(); listIterator.hasNext(); ) {
      ItemStack i = listIterator.next();
      if (i != null && 
        this.item.getTypeId() == i.getTypeId() && this.item.getData().getData() == i.getData().getData())
        return true; 
    } 
    return false;
  }
  
  public String isItemKit() {
    if (this.item == null)
      return null;

    ConfigurationSection path = Main.essentials.getConfig().getConfigurationSection("kits");

    if(path == null) return null;

    for (String key : Main.essentials.getConfig().getConfigurationSection("kits").getKeys(false)) {
      for (String key2 : Main.essentials.getConfig().getStringList("kits." + key + ".items")) {
        int id = Integer.parseInt(key2.split(" ")[0]);
        if (id == this.item.getTypeId())
          return "essentials.kits." + key; 
      } 
    } 
    return null;
  }
  
  public static void sendMessagePlayer(String playername, String msg) {
    if (Bukkit.getPlayerExact(playername) != null)
      Bukkit.getPlayerExact(playername).sendMessage(msg); 
  }
  
  public static ArrayList<Player> getStaff() {
    ArrayList<Player> staff = new ArrayList<>();
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.hasPermission("cylrp.staff"))
        staff.add(p); 
    } 
    return staff;
  }
  
  public boolean isOnPlayerInventoryByItemId(int amount) {
    int size = 0;
    for (ListIterator<ItemStack> listIterator = this.inv.iterator(); listIterator.hasNext(); ) {
      ItemStack i = listIterator.next();
      if (i != null && this.item.getTypeId() == i.getTypeId())
        size += this.item.getAmount(); 
    } 
    if (size >= amount)
      return true; 
    return false;
  }
  
  public boolean countOfItemIsInInventory(int count, ItemStack it) {
    int countof = count;
    for (ItemStack i : this.inv.getContents()) {
      if (i != null && i.getTypeId() == it.getTypeId() && i.getData().getData() == it.getData().getData()) {
        if (countof - i.getAmount() <= 0)
          return true; 
        countof -= i.getAmount();
      } 
    } 
    return false;
  }
  
  public static boolean isConnected(String name) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getName().equals(name))
        return true; 
    } 
    return false;
  }
  
  public static Player getPlayer(String name) {
    for (Player p : Main.INSTANCE.getServer().getWorld("cyl").getPlayers()) {
      if (p.getName().equals(name))
        return p; 
    } 
    return null;
  }
  
  public void removeItemFromInventory() {
    this.inv.removeItem(this.item);
  }
  
  public void removeItems(Material type, int amount) {
    if (amount <= 0)
      return; 
    int size = this.inv.getSize();
    for (int slot = 0; slot < size; slot++) {
      ItemStack is = this.inv.getItem(slot);
      if (is != null && 
        type == is.getType()) {
        int newAmount = is.getAmount() - amount;
        if (newAmount > 0) {
          is.setAmount(newAmount);
          break;
        } 
        this.inv.clear(slot);
        amount = -newAmount;
        if (amount == 0)
          break; 
      } 
    } 
  }
  
  public void removeItems(int id, int amount) {
    if (amount <= 0)
      return; 
    int size = this.inv.getSize();
    for (int slot = 0; slot < size; slot++) {
      ItemStack is = this.inv.getItem(slot);
      if (is != null && 
        id == is.getTypeId()) {
        int newAmount = is.getAmount() - amount;
        if (newAmount > 0) {
          is.setAmount(newAmount);
          break;
        } 
        this.inv.clear(slot);
        amount = -newAmount;
        if (amount == 0)
          break; 
      } 
    } 
  }
  
  public boolean isClose(Block b, double rayon) {
    if (this.p.getLocation().distance(b.getLocation()) <= rayon)
      return true; 
    return false;
  }
  
  public ArrayList<Player> getNearbyPlayers(double rayon) {
    ArrayList<Player> players = new ArrayList<>();
    for (Entity entity : this.p.getNearbyEntities(rayon, rayon, rayon)) {
      if (entity instanceof Player)
        players.add((Player)entity); 
    } 
    return players;
  }
  
  public ArrayList<Player> getNearbyPlayersByJob(double rayon, String job) {
    ArrayList<Player> players = new ArrayList<>();
    for (Entity entity : this.p.getNearbyEntities(rayon, rayon, rayon)) {
      if (entity instanceof Player) {
        Player p = (Player)entity;
        PlayerData pData = PlayerData.getPlayerData(p.getName());
        if (pData.selectedJob.getFeatures().getName().equalsIgnoreCase(job))
          players.add(p); 
      } 
    } 
    return players;
  }
  
  public boolean isItemOnInventory(ItemStack item) {
    if (item != null)
      for (ItemStack i : this.inv.getContents()) {
        if (i != null && 
          i.isSimilar(item))
          return true; 
      }  
    return false;
  }
  
  public boolean isOneOfItemsOnInv(List<ItemStack> items) {
    if (items != null)
      for (ItemStack item : items) {
        if (item != null)
          for (ItemStack i : this.inv.getContents()) {
            if (i != null && 
              i.getTypeId() == item.getTypeId() && i.getData().getData() == item.getData().getData())
              return true; 
          }  
      }  
    return false;
  }
  
  public boolean allItemsOnInventory(ArrayList<ItemStack> items) {
    for (ItemStack i : items) {
      if (!isItemOnInventory(i))
        return false; 
    } 
    return true;
  }
  
  public ArrayList<Player> getNearbyPlayersByJob(double rayon, String job, Player p) {
    ArrayList<Player> pjobs = new ArrayList<>();
    PlayerData clickerData = PlayerData.getPlayerData(p.getName());
    if (clickerData.selectedJob.getFeatures().getName().equalsIgnoreCase(job))
      pjobs.add(p); 
    for (Player player : getNearbyPlayers(rayon)) {
      PlayerData data = PlayerData.getPlayerData(player.getName());
      if (data.selectedJob.getFeatures().getName().equalsIgnoreCase(job))
        pjobs.add(player); 
    } 
    return pjobs;
  }
  
  public static void teleportToWarp(Player p, String warp) {
    Main.INSTANCE.getServer().dispatchCommand((CommandSender)Main.INSTANCE.getServer().getConsoleSender(), "warp " + warp + " " + p.getName());
  }
  
  public static void teleportToSpawn(Player p) {
    Main.INSTANCE.getServer().dispatchCommand((CommandSender)Main.INSTANCE.getServer().getConsoleSender(), "warp spawn " + p.getName());
  }
  
  public boolean isClose(Entity entity, int rayon) {
    if (entity != null && this.p.getLocation().distance(entity.getLocation()) <= rayon)
      return true; 
    return false;
  }
}
