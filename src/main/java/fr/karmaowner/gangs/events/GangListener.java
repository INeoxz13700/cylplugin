package fr.karmaowner.gangs.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.cylapi.events.PlayerKilledByPlayerEnterReanimationEvent;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.gangs.Capture;
import fr.karmaowner.utils.RegionUtils;

import java.util.Iterator;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GangListener implements Listener {
  @EventHandler(priority = EventPriority.LOWEST)
  public void onInteractPlayer(EntityDamageByEntityEvent e) {
    Entity dmg = e.getDamager();
    Entity vict = e.getEntity();
    Player damager = null;
    if (vict instanceof Player) {
      if (dmg instanceof Player) {
        damager = (Player)dmg;
      } else if (dmg instanceof Projectile) {
        Projectile arrow = (Projectile)dmg;
        if (arrow.getShooter() instanceof Player) {
          damager = (Player)arrow.getShooter();
        } else {
          return;
        } 
      } else {
        return;
      } 
    } else {
      return;
    } 
    Player victim = (Player)vict;
    PlayerData dataVictim = PlayerData.getPlayerData(victim.getName());
    PlayerData dataDamager = PlayerData.getPlayerData(damager.getName());
    if (dataDamager.gangName != null && !dataDamager.gangName.equals("") && dataVictim.gangName != null && 
      !dataVictim.gangName.equals("") && !(dataVictim.selectedJob instanceof fr.karmaowner.jobs.Legal) && 
      !dataVictim.selectedJob.isOutOfService() && !(dataDamager.selectedJob instanceof fr.karmaowner.jobs.Legal) && 
      !dataDamager.selectedJob.isOutOfService())
      Main.INSTANCE.getServer().getPluginManager().callEvent(new DamageGangPlayerEvent(damager, victim, GangData.getGang(dataVictim.gangName), e)); 
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerKilledByPlayerEnterReanimationEvent event) {
    PlayerData killerData = PlayerData.getPlayerData(event.getKiller().getName());
    PlayerData victimData = PlayerData.getPlayerData(event.getVictim().getName());
    if (killerData.hasGang() && victimData.hasGang()) {
      GangData killerGangData = GangData.getGangData(killerData.gangName);
      GangData victimGangData = GangData.getGangData(victimData.gangName);
      if (victimData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste && killerData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste)
        if (killerGangData.isEnemyWith(victimGangData.getGangName()) || victimGangData.isEnemyWith(killerGangData.getGangName())) {
          Set<ProtectedRegion> set = RegionUtils.getRegionManager(event.getVictim().getWorld().getName()).getApplicableRegions(event.getVictim().getLocation()).getRegions();
          Iterator<ProtectedRegion> iterator = set.iterator();
          while (iterator.hasNext()) {
            ProtectedRegion region = iterator.next();
            if (Capture.isCaptureExist(region.getId())) {
              killerGangData.setRankingPoints(killerGangData.getRankingPoints() + 1);
              victimGangData.setRankingPoints(victimGangData.getRankingPoints() - 1);
              victimGangData.sendMessageAll("§c[Guerre de gang] §b" + event.getVictim().getName() + " §c est mort lors d'un affrontement contre le gang ennemie §b" + killerGangData.getGangName() + " §4-1 §cPts dans votre classement");
              killerGangData.sendMessageAll("§c[Guerre de gang] §b" + event.getKiller().getName() + " §a a tué un ennemie lors d'un affrontement contre le gang ennemie §b" + victimGangData.getGangName() + " §2+1 §aPts dans votre classement");
              return;
            } 
          } 
        }  
    } 
  }
}
