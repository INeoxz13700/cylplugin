package fr.karmaowner.events;

import fr.karmaowner.casino.Casino;
import fr.karmaowner.casino.RouletteGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RouletteEvents extends CasinoEvents {
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    ItemStack item = event.getCurrentItem();
    Player p = (Player)event.getWhoClicked();
    Casino c = Casino.getGame(p);
    if (c instanceof RouletteGame) {
      RouletteGame r = (RouletteGame)c;
      if (event.getInventory().getName().equals(c.inv.getName())) {
        if (r.multiply.get(p) == null) {
          if (item.getType() == Material.STAINED_GLASS_PANE && item.getData().getData() == 14) {
            r.multiply.put(p, r.itemRatio[0]);
          } else if (item.getType() == Material.STAINED_GLASS_PANE && item.getData().getData() == 3) {
            r.multiply.put(p, r.itemRatio[1]);
          } else if (item.getType() == Material.STAINED_GLASS_PANE && item.getData().getData() == 4) {
            r.multiply.put(p, r.itemRatio[2]);
          } 
          p.sendMessage("Vous avez choisi de multiplier par x" + r.multiply.get(p) + " la cagnotte.");
        } 
        event.setCancelled(true);
      } 
    } 
  }
}
