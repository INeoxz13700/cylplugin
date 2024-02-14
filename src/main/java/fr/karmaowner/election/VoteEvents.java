package fr.karmaowner.election;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VoteEvents implements Listener {
  @EventHandler
  public void voter(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    Inventory inv = e.getInventory();
    ItemStack item = e.getCurrentItem();
    if (inv.getName().equalsIgnoreCase(Vote.inv.getName())) {
      String candidat = Vote.getCandidatByItemName(item.getItemMeta().getDisplayName());
      if (candidat != null) {
        Vote.voter(p.getName(), candidat);
        p.closeInventory();
      } 
      e.setCancelled(true);
    } 
  }
}
