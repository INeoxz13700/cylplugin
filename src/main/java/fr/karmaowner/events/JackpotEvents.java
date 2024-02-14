package fr.karmaowner.events;

import fr.karmaowner.casino.Casino;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class JackpotEvents extends CasinoEvents {
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Player p = (Player)event.getWhoClicked();
    Casino c = Casino.getGame(p);
    if (c instanceof fr.karmaowner.casino.Jackpot && event.getInventory().getName().equals(c.inv.getName()))
      event.setCancelled(true); 
  }
}
