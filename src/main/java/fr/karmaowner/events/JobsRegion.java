package fr.karmaowner.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.grades.Grade;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.RegionUtils;
import java.util.ArrayList;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class JobsRegion implements Listener {
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    Location l = p.getLocation();
    Location lLoc = p.getEyeLocation();
    Jobs.Job[] jobs = Jobs.Job.values();
    if (event.getClickedBlock() == null)
      return; 
    Set<ProtectedRegion> rgs = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(event.getClickedBlock().getLocation()).getRegions();
    if (p.hasPermission("cylrp.region.bypass"))
      return; 
    for (ProtectedRegion region : rgs) {
      String rgname = region.getId().toLowerCase();
      String[] rang_metier = rgname.split("_");
      if (rang_metier.length >= 2) {
        String rang = rang_metier[1];
        String str1 = rang_metier[0];
        if (!Jobs.Job.regionNameContainsJob(str1))
          continue; 
        if (rang.toLowerCase().contains("connectes")) {
          if (Jobs.Job.getJobByName(str1).onlineServicePlayers().size() > 0) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cZone innaccessible en raison de la présence de " + str1);
          } 
          return;
        } 
        if (region.getId().toLowerCase().matches("^[a-z]+_[0-9]+")) {
          if (!str1.toLowerCase().equals(pData.selectedJob.getFeatures().getName())) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          } 
          return;
        } 
        if (pData.selectedJob instanceof hasGrade) {
          Grade grade = ((hasGrade)pData.selectedJob).getGrade().getGrade();
          if (!rang.equalsIgnoreCase(grade.getNom().toLowerCase()) || !str1.equalsIgnoreCase(pData.selectedJob.getFeatures().getName().toLowerCase())) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier ou rang nécessaire pour intéragir dans cette région");
            return;
          } 
          continue;
        } 
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier ou rang nécessaire pour intéragir dans cette région");
        return;
      } 
      if (rgname.toLowerCase().contains("bank") || rgname.toLowerCase().contains("bijouterie")) {
        if (!(pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Voleur) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Hacker) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Medecin)) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          return;
        } 
        ArrayList<Player> players = Jobs.Job.onlinePlayers(Jobs.Job.GENDARME, Jobs.Job.DOUANIER, Jobs.Job.MEDECIN, Jobs.Job.BAC, Jobs.Job.MILITAIRE, Jobs.Job.GIGN, Jobs.Job.POMPIER);
        if (players.size() < 2) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cPour réaliser cette action il doit y avoir au moins 2 gendarmes de connecté");
          return;
        } 
        continue;
      } 
      if (rgname.toLowerCase().contains("marchenoir")) {
        if (!pData.selectedJob.getFeatures().isIllegal()) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          return;
        } 
        continue;
      } 
      String metier = rgname;
      if (!Jobs.Job.regionNameContainsJob(metier))
        continue; 
      if (!metier.toLowerCase().equals(pData.selectedJob.getFeatures().getName())) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
        return;
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    final Player p = (Player)event.getPlayer();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    Location l = p.getLocation();
    Location lLoc = p.getEyeLocation();
    Jobs.Job[] jobs = Jobs.Job.values();
    Set<ProtectedRegion> rgs = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(l).getRegions();
    if (p.hasPermission("cylrp.region.bypass"))
      return; 
    for (ProtectedRegion region : rgs) {
      String rgname = region.getId().toLowerCase();
      String[] rang_metier = rgname.split("_");
      if (rang_metier.length >= 2) {
        String rang = rang_metier[1];
        String str1 = rang_metier[0];
        if (!Jobs.Job.regionNameContainsJob(str1))
          continue; 
        if (rang.toLowerCase().contains("connectes")) {
          if (Jobs.Job.getJobByName(str1).onlineServicePlayers().size() > 0) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cZone innaccessible en raison de la présence de " + str1);
          } 
          return;
        } 
        if (region.getId().toLowerCase().matches("^[a-z]+_[0-9]+")) {
          if (!str1.toLowerCase().equals(pData.selectedJob.getFeatures().getName())) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          } 
          return;
        } 
        if (pData.selectedJob instanceof hasGrade) {
          Grade grade = ((hasGrade)pData.selectedJob).getGrade().getGrade();
          if (!rang.equalsIgnoreCase(grade.getNom().toLowerCase()) || !str1.equalsIgnoreCase(pData.selectedJob.getFeatures().getName().toLowerCase())) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier ou rang nécessaire pour intéragir dans cette région");
            return;
          } 
          continue;
        } 
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier ou rang nécessaire pour intéragir dans cette région");
        return;
      } 
      if (rgname.toLowerCase().contains("bank") || rgname.toLowerCase().contains("bijouterie")) {
        if (!(pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Voleur) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Hacker) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Medecin)) {
          new TaskCreator(new CustomRunnable() {
                public void customRun() {
                  p.closeInventory();
                }
              },  false, 20L);
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          return;
        } 
        continue;
      } 
      if (rgname.toLowerCase().contains("marchenoir")) {
        if (!pData.selectedJob.getFeatures().isIllegal()) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          return;
        } 
        continue;
      } 
      String metier = rgname;
      if (!Jobs.Job.regionNameContainsJob(metier))
        continue; 
      if (!metier.toLowerCase().equals(pData.selectedJob.getFeatures().getName())) {
        new TaskCreator(new CustomRunnable() {
              public void customRun() {
                p.closeInventory();
              }
            },  false, 20L);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
        return;
      } 
    } 
  }
  
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    Player p = event.getPlayer();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    Location l = p.getLocation();
    Location lLoc = p.getEyeLocation();
    Jobs.Job[] jobs = Jobs.Job.values();
    Entity entity = event.getRightClicked();
    Set<ProtectedRegion> rgs = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(entity.getLocation()).getRegions();
    if (p.hasPermission("cylrp.region.bypass"))
      return; 
    for (ProtectedRegion region : rgs) {
      String rgname = region.getId().toLowerCase();
      String[] rang_metier = rgname.split("_");
      if (rang_metier.length >= 2) {
        String rang = rang_metier[1];
        String str1 = rang_metier[0];
        if (!Jobs.Job.regionNameContainsJob(str1))
          continue; 
        if (rang.toLowerCase().contains("connectes")) {
          if (Jobs.Job.getJobByName(str1).onlineServicePlayers().size() > 0) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cZone innaccessible en raison de la présence de " + str1);
          } 
          return;
        } 
        if (region.getId().toLowerCase().matches("^[a-z]+_[0-9]+")) {
          if (!str1.toLowerCase().equals(pData.selectedJob.getFeatures().getName())) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          } 
          return;
        } 
        if (pData.selectedJob instanceof hasGrade) {
          Grade grade = ((hasGrade)pData.selectedJob).getGrade().getGrade();
          if (!rang.equalsIgnoreCase(grade.getNom().toLowerCase()) || !str1.equalsIgnoreCase(pData.selectedJob.getFeatures().getName().toLowerCase())) {
            event.setCancelled(true);
            MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier ou rang nécessaire pour intéragir dans cette région");
            return;
          } 
          continue;
        } 
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier ou rang nécessaire pour intéragir dans cette région");
        return;
      } 
      if (rgname.toLowerCase().contains("bank") || rgname.toLowerCase().contains("bijouterie")) {
        if (!(pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Voleur) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Hacker) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Medecin)) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          return;
        } 
        if (Jobs.Job.GENDARME.onlinePlayers().size() < 1) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cPour réaliser cette action il doit y avoir au moins 2 gendarmes de connecté");
          return;
        } 
        continue;
      } 
      if (rgname.toLowerCase().contains("marchenoir")) {
        if (!pData.selectedJob.getFeatures().isIllegal()) {
          event.setCancelled(true);
          MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
          return;
        } 
        continue;
      } 
      String metier = rgname;
      if (!Jobs.Job.regionNameContainsJob(metier))
        continue; 
      if (!metier.toLowerCase().equals(pData.selectedJob.getFeatures().getName())) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'avez pas le métier nécessaire pour intéragir dans cette région");
        return;
      } 
    } 
  }
}
