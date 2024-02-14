package fr.karmaowner.events;

import fr.karmaowner.wantedlist.WantedList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class WantedListEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    Inventory inv = e.getClickedInventory();
    if (inv != null && 
      inv.getName().equalsIgnoreCase(WantedList.inventoryName))
      e.setCancelled(true); 
  }
}
