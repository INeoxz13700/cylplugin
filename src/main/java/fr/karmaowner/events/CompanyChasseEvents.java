package fr.karmaowner.events;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyChasse;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CompanyChasseEvents implements Listener {
  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent e) {


    Entity entity = e.getDamager();
    if (entity instanceof Projectile || entity instanceof Player) {
      Player p = null;
      if (entity instanceof Player) {
        p = (Player)entity;
        if(p.hasPermission(Permissions.Staff)) return;
      } else {
        Projectile arrow = (Projectile)entity;
        if (arrow.getShooter() instanceof Player) {
          p = (Player)arrow.getShooter();
        } else {
          return;
        }
      } 
      attachToEvent(p, e);
    } 
  }
  
  public void attachToEvent(final Player p, EntityDamageByEntityEvent e) {
    final PlayerData pdata = PlayerData.getPlayerData(p.getName());
    if (pdata == null)
      return; 
    CompanyData data = CompanyData.Companies.get(pdata.companyName);
    if (data != null) {
      if (data.getCompany() != null)
        if (data.getCompany() instanceof CompanyChasse) {
          if (!(data.getCompany()).isThatRegion) {
            final CompanyChasse ch = (CompanyChasse)data.getCompany();
            if (ch == null)
              return; 
            final Entity entityDie = e.getEntity();
            final Company.XP x = ch.toXP(entityDie.getType());
            if (x != null)
              if (ch.isItemUnlocked(x)) {
                if (pdata.selectedJob != null && pdata.selectedJob.getTask() == null) {
                  pdata.selectedJob.setTask(new TaskCreator(new CustomRunnable() {
                          private int timer = 10;
                          
                          public void customRun() {
                            if (pdata.selectedJob.getTask() == null)
                              cancel(); 
                            if (--this.timer <= 0) {
                              pdata.selectedJob.setTask(null);
                              cancel();
                            } 
                            if (entityDie.isDead()) {
                              ch.addXp(p, x);
                              ch.setCompanyAchievements();
                              pdata.selectedJob.setTask(null);
                              cancel();
                            } 
                          }
                        }, false, 0L, 20L));
                  pdata.selectedJob.getTask().attachObject(entityDie);
                } else {
                  Entity ent = (Entity)pdata.selectedJob.getTask().getObj();
                  if (ent.getEntityId() != entityDie.getEntityId()) {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.RED + "Une tâche est déjà en cours. Veuillez patienter avant de tuer le mob.");
                  } 
                } 
              } else {
                e.setCancelled(true);
                ch.locked_Message(p, x);
              }  
          } 
        } else {
          for (CompanyChasse.XP_CHASSE chasse : CompanyChasse.XP_CHASSE.values()) {
            if (chasse.getType() == e.getEntity().getType()) {
              e.setCancelled(true);
              return;
            } 
          } 
        }  
    } else {
      for (CompanyChasse.XP_CHASSE chasse : CompanyChasse.XP_CHASSE.values()) {
        if (chasse.getType() == e.getEntity().getType()) {
          e.setCancelled(true);
          return;
        } 
      } 
    } 
  }
}
