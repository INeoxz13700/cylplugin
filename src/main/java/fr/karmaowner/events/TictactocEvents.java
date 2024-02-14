package fr.karmaowner.events;

import fr.karmaowner.casino.Casino;
import fr.karmaowner.casino.TictactocGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TictactocEvents extends CasinoEvents {
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Player player = (Player)event.getWhoClicked();
    Inventory inventory = event.getInventory();
    ItemStack Itemclicked = event.getCurrentItem();
    Casino c = Casino.getGame(player);
    if (c instanceof TictactocGame) {
      TictactocGame t = (TictactocGame)c;
      Material TeamItem = null;
      String teamName = null;
      if (player.equals(t.p1)) {
        TeamItem = t.t1.getId();
        teamName = t.t1.getString();
      } else {
        TeamItem = t.t2.getId();
        teamName = t.t2.getString();
      } 
      if (inventory.getName().equals(t.inv.getName()) && 
        event.isLeftClick() && 
        player.equals(t.tour)) {
        int slotClicked = event.getSlot();
        if ((slotClicked >= 3 && slotClicked <= 5) || (slotClicked >= 12 && slotClicked <= 14) || (slotClicked >= 21 && slotClicked <= 23))
          if (Itemclicked.getType() == Material.AIR) {
            t.tempsRestant = t.temps;
            t.addItemInventory(TeamItem, 1, slotClicked, teamName);
            if (t.tour.equals(t.p1)) {
              t.casesT1.set(t.physicCase[slotClicked]);
              t.tour = t.p2;
            } else {
              t.casesT2.set(t.physicCase[slotClicked]);
              t.tour = t.p1;
            } 
            if (t.checkIfWinner() != null)
              t.endGame(t.checkIfWinner()); 
          }  
      } 
      event.setCancelled(true);
    } 
  }
}
