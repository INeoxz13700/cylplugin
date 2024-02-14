package fr.karmaowner.jobs.hacker;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Hacker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MakeHackingGameEvents implements Listener {
  @EventHandler
  public void onCloseInventory(InventoryCloseEvent e) {
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data != null && data.selectedJob instanceof Hacker) {
      Hacker h = (Hacker)data.selectedJob;
      final MakeHackingGame game = h.getGame();
      if (game != null && 
        inventory.getTitle().equals(game.getTitle()) && !game.getFinished())
        new TaskCreator(new CustomRunnable() {
              public void customRun() {
                if (Bukkit.getPlayerExact(game.getPlayer().getName()) == null) {
                  cancel();
                  return;
                } 
                game.end();
                game.getPlayer().sendMessage(ChatColor.RED + "Vous avez échoué ! Réessayez plus tard !");
                cancel();
              }
            },  false, 1L, 0L); 
    } 
  }
  
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    ItemStack item = e.getCurrentItem();
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof Hacker) {
      Hacker h = (Hacker)data.selectedJob;
      MakeHackingGame game = h.getGame();
      if (game != null && 
        inventory.getTitle().equals(game.getTitle())) {
        if (type == ClickType.LEFT && 
          item != null && 
          item.getType() != Material.AIR)
          game.winGame(item, e.getSlot()); 
        e.setCancelled(true);
      } 
    } 
  }
}
