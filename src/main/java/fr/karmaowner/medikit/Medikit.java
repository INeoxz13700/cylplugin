package fr.karmaowner.medikit;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import java.math.BigDecimal;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Medikit implements Listener {
  public static final float healGain = 200.0F;
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (player.getInventory().getItemInHand() == null)
      return; 
    if (!player.getInventory().getItemInHand().hasItemMeta())
      return; 
    if (!player.getInventory().getItemInHand().getItemMeta().hasDisplayName())
      return; 
    if (player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§aMedikit"))
      event.setCancelled(true); 
  }
  
  @EventHandler
  public void onClickMedikit(PlayerInteractEntityEvent event) {
    if (event.getRightClicked() instanceof Player) {
      Player target = (Player)event.getRightClicked();
      Player player = event.getPlayer();
      
      if (player.getInventory().getItemInHand() == null)
        return; 
      if (!player.getInventory().getItemInHand().hasItemMeta())
        return; 
      if (!player.getInventory().getItemInHand().getItemMeta().hasDisplayName())
        return;

      if (player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§aMedikit"))
      {
        if (((Damageable)target).getHealth() < 20.0D) {
            final PlayerData data = PlayerData.getPlayerData(player.getName());
            if (data.selectedJob.getFeatures() != Jobs.Job.MEDECIN) {
              player.sendMessage("§cSeul les médecins peuvent utiliser les medikits");
              return;
            }
            PlayerData targetData = PlayerData.getPlayerData(target.getName());
            if (player.getLocation().distance(target.getLocation()) >= 2.0D) {
              player.sendMessage("§cRapprochez-vous de votre cible!");
              return;
            }
            if (targetData.isDeath || targetData.getUsingMedikit()) {
              player.sendMessage("§cCe joueur est en réanimation");
              return;
            }
            if (!data.getUsingMedikit()) {
              data.setUsingMedikit(true);
              event.setCancelled(true);
              new TaskCreator(new CustomRunnable() {
                    private int percent = 0;

                    public void customRun() {
                      if (player.getInventory().getItemInHand() == null || !player.getInventory().getItemInHand().hasItemMeta() || !player.getInventory().getItemInHand().getItemMeta().hasDisplayName() || !player.getInventory().getItemInHand().getItemMeta().getDisplayName().equals("§aMedikit")) {
                        player.sendMessage(ChatColor.RED + "Utilisation du medikit annulée: Vous devez garder en main le medikit !");
                        data.setUsingMedikit(false);
                        cancel();
                        return;
                      }
                      if (!target.isOnline() || !player.isOnline()) {
                        data.setUsingMedikit(false);
                        cancel();
                        return;
                      }
                      if (player.getLocation().distance(target.getLocation()) >= 2.0D) {
                        player.sendMessage(ChatColor.RED + "Utilisation du medikit annulée: Votre cible s'est éloigné !");
                        data.setUsingMedikit(false);
                        cancel();
                        return;
                      }
                      if (!data.getUsingMedikit()) {
                        cancel();
                        return;
                      }
                      this.percent += 5;
                      if (this.percent >= 100) {
                        data.setUsingMedikit(false);
                        target.setHealth(20);
                        player.getInventory().removeItem(player.getInventory().getItemInHand());
                        player.sendMessage(ChatColor.GREEN + "Medikit utilisé !");
                        data.setMoney(data.getMoney().add(BigDecimal.valueOf(200.0D)));
                        player.sendMessage("§eFélicitations vous venez de remporter : §6200.0 §eeuro");
                        cancel();
                      } else {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Vous êtes entrain de soigner : " + ChatColor.DARK_PURPLE + this.percent + "%");
                        target.sendMessage(ChatColor.LIGHT_PURPLE + "Vous êtes entrain de vous faire soigner : " + ChatColor.DARK_PURPLE + this.percent + "%");
                      }
                    }
                  },  false, 0L, 40L);
            } else {
              player.sendMessage(ChatColor.RED + "Vous êtes déjà en train d'utiliser un medikit !");
            }
          }
          else {
            player.sendMessage("§cCe joueur n'a pas besoin d'être soigner");
          }
      }
    } 
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if ((e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) && 
      data.getUsingMedikit()) {
      p.sendMessage(ChatColor.RED + "Utilisation du medikit annulée !");
      data.setUsingMedikit(false);
    } 
  }
}
