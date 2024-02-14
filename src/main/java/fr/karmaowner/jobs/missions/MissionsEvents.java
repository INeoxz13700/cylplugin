package fr.karmaowner.jobs.missions;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.missions.type.GeneralType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MissionsEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (!data.selectedJob.isOutOfService() && 
      item != null && 
      item.getType() != Material.AIR && 
      inventory.getName().equals(Missions.MISSIONSINVNAME)) {
      Missions missions = (Missions)data.selectedJob;
      if (missions.getInProgress() == null) {
        Mission m = missions.getMissionByItem(item);
        if (m != null) {
          missions.setInProgress(m);
          p.closeInventory();
          p.sendMessage(ChatColor.GREEN + "Mission acceptée !");
          p.sendMessage(m.getObjective());
          GeneralType gt = (GeneralType)m.getType();
          gt.startTask(p);
        } 
      } else {
        p.sendMessage(ChatColor.RED + "Vous avez une missions en cours !");
      } 
      e.setCancelled(true);
    } 
  }
}
