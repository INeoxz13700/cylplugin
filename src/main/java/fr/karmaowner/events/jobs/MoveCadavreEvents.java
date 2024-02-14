package fr.karmaowner.events.jobs;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Civile;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.utils.CustomConcurrentHashMap;
import fr.karmaowner.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MoveCadavreEvents implements Listener {
  public static CustomConcurrentHashMap<String, Entity> cadavresTarggeted = new CustomConcurrentHashMap();
  
  @EventHandler
  public void onClickCadavre(InventoryClickEvent e) {
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    final PlayerData data = PlayerData.getPlayerData(p.getName());
    if (item != null && 
      item.getType() != Material.AIR && 
      inventory.getName().equals(Jobs.NAMEACTIONINVENTORY) && 
      item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(Civile.Action.CADAVRE.getDisplayName())) {
      Entity targetted = data.selectedJob.getEntityTarget();
      if (targetted != null)
        if (!cadavresTarggeted.containsValue(targetted)) {
          cadavresTarggeted.put(p.getName(), targetted);
          MessageUtils.sendMessage((CommandSender)p, "§aVous êtes en train de portez le cadavre. Vous pouvez le relâcher en faisant clic-droit dessus");
          data.selectedJob.setTask(new TaskCreator(new CustomRunnable() {
                  public void customRun() {
                    Entity target = (Entity)MoveCadavreEvents.cadavresTarggeted.get(p.getName());
                    if (target == null) {
                      cancel();
                      data.selectedJob.setTask(null);
                      return;
                    } 
                    if (data.isDeath) {
                      cancel();
                      data.selectedJob.setTask(null);
                      return;
                    } 
                    target.teleport(p.getEyeLocation());
                  }
                },  false, 0L, 20L));
        } else if (cadavresTarggeted.get(p.getName()) == null || cadavresTarggeted
          .get(p.getName()) != targetted) {
          MessageUtils.sendMessage((CommandSender)p, "Ce cadavre est déjà sous l'autorité d'un individu");
        }  
    } 
  }
  
  @EventHandler
  public void RelacherCadavre(PlayerInteractEntityEvent e) {
    Entity target = e.getRightClicked();
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (cadavresTarggeted.get(p.getName()) != null && cadavresTarggeted
      .get(p.getName()) == target) {
      cadavresTarggeted.remove(p.getName());
      p.closeInventory();
      MessageUtils.sendMessage((CommandSender)p, "§aLe cadavre vient d'être relâché");
    } 
  }
}
