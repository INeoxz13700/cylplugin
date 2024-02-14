package fr.karmaowner.jobs.voleur;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.EventRegistererData;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.utils.ItemUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Braquage extends EventRegistererData {
  private Inventory inv;
  
  private String inventoryName;
  
  private String configName;
  
  private ArrayList<ItemStack> loadedItems;
  
  private Map<Integer, ItemStack> items;
  
  protected ItemStack loadingItem = ItemUtils.getItem(160, (byte)7, 1, "§cchargement en cours...", null);
  
  protected ItemStack loadedItem = ItemUtils.getItem(160, (byte)14, 1, "§4chargé", null);
  
  private volatile boolean isLoading;
  
  private Player p;
  
  private int[] loadingSlots = new int[] { 
      2, 3, 4, 5, 6, 15, 24, 33, 42, 51, 
      50, 49, 48, 47, 38, 29, 20, 11, 12, 13, 
      14, 23, 32, 41, 40, 39, 30, 21, 22, 31 };
  
  public Braquage(String inventoryName, String configName, Player p) {
    this.inv = Bukkit.createInventory(null, 54, inventoryName);
    this.configName = configName;
    this.inventoryName = inventoryName;
    this.p = p;
    this.loadedItems = new ArrayList<>();
    this.isLoading = true;
    loadData();
    loadingInventory();
  }
  
  public Inventory getInventory() {
    return this.inv;
  }
  
  public String getInventoryName() {
    return this.inventoryName;
  }
  
  public void fillInventory() {
    this.inv.clear();
    if (this.items != null)
      for (Map.Entry<Integer, ItemStack> entry : this.items.entrySet())
        this.inv.setItem(entry.getKey(), entry.getValue());
  }
  
  public void loadingInventory() {
    this.inv.clear();
    for (int slot : this.loadingSlots)
      this.inv.setItem(slot, this.loadingItem); 
  }
  
  public void start() {
    this.p.openInventory(this.inv);
    loadingTask();
  }
  
  public boolean isLoaded() {
    for (int slot : this.loadingSlots) {
      if (this.inv.getItem(slot).getItemMeta().getDisplayName().equals(this.loadingItem.getItemMeta().getDisplayName()))
        return false; 
    } 
    return true;
  }
  
  public void loadingTask() {
    new TaskCreator(new CustomRunnable() {
          private int i = 0;
          
          public void customRun() {
            if (Braquage.this.p == null) {
              cancel();
              return;
            } 
            if (Braquage.this.isLoaded()) {
              Braquage.this.isLoading = false;
              cancel();
              Braquage.this.fillInventory();
              return;
            } 
            Braquage.this.inv.setItem(Braquage.this.loadingSlots[this.i], Braquage.this.loadedItem);
            this.i++;
          }
        }, false, 0L, 10L);
  }
  
  private Map<Integer, ItemStack> shuffleItemsLocation(ArrayList<ItemStack> items) {
    Map<Integer, ItemStack> shuffledItems = new HashMap<>();
    ArrayList<Integer> locations = new ArrayList<>();
    for (int i = 0; i < 54; i++)
      locations.add(i);
    for (ItemStack item : items) {
      int randomLocation = randomLocationItem(locations);
      shuffledItems.put(randomLocation, item);
    } 
    return shuffledItems;
  }
  
  private int randomLocationItem(ArrayList<Integer> locations) {
    int randomLocation = (int)(Math.random() * locations.size() - 1.0D);
    locations.remove(randomLocation);
    return randomLocation;
  }
  
  public boolean isLoading() {
    return this.isLoading;
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public ArrayList<ItemStack> getItems() {
    return this.loadedItems;
  }
  
  public void loadData() {
    if (getFileConfig().getStringList("Braquage." + this.configName) != null) {
      for (String item : getFileConfig().getStringList("Braquage." + this.configName)) {
        String[] splitted = item.split(":");
        if (splitted.length > 2) {
          int id = Integer.parseInt(splitted[0]);
          byte data = Byte.parseByte(splitted[1]);
          int amount = Integer.parseInt(splitted[2]);
          this.loadedItems.add(ItemUtils.getItem(id, data, amount, null, null));
        } 
      } 
      this.items = shuffleItemsLocation(this.loadedItems);
    } 
  }
  
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    Inventory inv = e.getClickedInventory();
    if (inv != null && inv.getTitle() != null && inv.getTitle().equalsIgnoreCase(this.inventoryName) && 
      this.isLoading)
      e.setCancelled(true); 
  }
}
