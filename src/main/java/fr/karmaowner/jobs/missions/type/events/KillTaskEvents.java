package fr.karmaowner.jobs.missions.type.events;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.missions.Missions;
import fr.karmaowner.jobs.missions.type.KillTask;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillTaskEvents implements Listener {
  @EventHandler
  public void onDamagePlayer(EntityDamageByEntityEvent e) {
    EntityType type = e.getEntityType();
    Entity entity = e.getDamager();
    if (entity instanceof Projectile || entity instanceof Player) {
      Player damager = null;
      if (entity instanceof Player) {
        damager = (Player)entity;
      } else if (entity instanceof Projectile) {
        Projectile arrow = (Projectile)entity;
        if (arrow.getShooter() instanceof Player) {
          damager = (Player)arrow.getShooter();
        } else {
          return;
        } 
      } else {
        return;
      } 
      PlayerData dataDamager = PlayerData.getPlayerData(damager.getName());
      if (dataDamager.selectedJob instanceof Missions) {
        Missions missions = (Missions)dataDamager.selectedJob;
        if (missions.getInProgress() != null && 
          missions.getInProgress().getType() instanceof KillTask) {
          KillTask kt = (KillTask)missions.getInProgress().getType();
          if (kt.getEntity() == EntityType.PLAYER && 
            type == EntityType.PLAYER) {
            Player victim = (Player)e.getEntity();
            PlayerData dataVictim = PlayerData.getPlayerData(victim.getName());
            if (dataVictim.isDeath && 
              kt.isJob(dataVictim.selectedJob.getFeatures()) && 
              kt.getTempCount() > 0)
              kt.setTempCount(kt.getTempCount() - 1); 
          } 
        } 
      } 
    } 
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    LivingEntity entity = event.getEntity();
    Player killer = entity.getKiller();
    if (killer != null) {
      PlayerData data = PlayerData.getPlayerData(killer.getName());
      if (data.selectedJob instanceof Missions) {
        Missions missions = (Missions)data.selectedJob;
        if (missions.getInProgress() != null && 
          missions.getInProgress().getType() instanceof KillTask) {
          KillTask kt = (KillTask)missions.getInProgress().getType();
          if (kt.getEntity() == entity.getType() && 
            kt.getTempCount() > 0)
            kt.setTempCount(kt.getTempCount() - 1); 
        } 
      } 
    } 
  }
}
