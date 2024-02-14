package fr.karmaowner.events.jobs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Douanier;
import java.math.BigDecimal;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DouanierEvents implements Listener {
  @EventHandler
  public void onDeath(EntityDamageByEntityEvent e) {
    Entity victim = e.getEntity();
    Entity damager = e.getDamager();
    Player vPlayer = null;
    Player dPlayer = null;
    if (damager instanceof Projectile && victim instanceof Player) {
      Projectile projectile = (Projectile)damager;
      if (projectile.getShooter() instanceof Player) {
        dPlayer = (Player)projectile.getShooter();
        vPlayer = (Player)victim;
      } else {
        return;
      } 
    } else if (victim instanceof Player && damager instanceof Player) {
      vPlayer = (Player)victim;
      dPlayer = (Player)damager;
    } else {
      return;
    } 
    final PlayerData dData = PlayerData.getPlayerData(dPlayer.getName());
    final PlayerData vData = PlayerData.getPlayerData(vPlayer.getName());
    if (dData.selectedJob instanceof Douanier) {
      final Douanier douanier = (Douanier)dData.selectedJob;
      if (!dData.selectedJob.isOutOfService()) {
        if (vData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) {
          boolean isRegion = false;
          for (ProtectedRegion r : Main.WG.getRegionManager(dPlayer.getWorld()).getApplicableRegions(dPlayer.getLocation())) {
            if (Douanier.isRegion(r.getId())) {
              isRegion = true;
              break;
            } 
          } 
          for (ProtectedRegion r : Main.WG.getRegionManager(vPlayer.getWorld()).getApplicableRegions(vPlayer.getLocation())) {
            if (Douanier.isRegion(r.getId())) {
              isRegion = true;
              break;
            } 
          } 
          if (isRegion)
            if (vData.isDeath && douanier.killed.get(dData.getPlayerName()) == null) {
              douanier.killed.put(dData.getPlayerName(), new TaskCreator(new CustomRunnable() {
                      public void customRun() {
                        if (!vData.isDeath) {
                          douanier.killed.remove(dData.getPlayerName());
                          cancel();
                        } 
                      }
                    },  false, 0L, 100L));
              double random = (int)(Math.random() * 1000.0D + 1000.0D);
              dData.setMoney(dData.getMoney().add(BigDecimal.valueOf(random)));
              dPlayer.sendMessage(ChatColor.GREEN + "Vous venez d'éliminer un " + vData.selectedJob
                  .getFeatures().getDisplayName() + ChatColor.GREEN + " pour cause de protection de la douane");
              dPlayer.sendMessage(ChatColor.GREEN + "Vous venez de remporter la somme " + ChatColor.DARK_GREEN + random + "€" + ChatColor.GREEN + " en guise de récompense !");
            } else {
              dPlayer.sendMessage(ChatColor.RED + "Vous avez déjà éliminé cette individu.");
            }  
        } 
      } else {
        dPlayer.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas en service pour exercer votre job.");
      } 
    } 
  }
}
