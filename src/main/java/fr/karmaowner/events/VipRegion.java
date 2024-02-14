package fr.karmaowner.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.RegionUtils;
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

public class VipRegion implements Listener {
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    Location l = p.getLocation();
    Location lLoc = p.getEyeLocation();
    Jobs.Job[] jobs = Jobs.Job.values();
    Set<ProtectedRegion> rgs = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(p.getLocation()).getRegions();
    if (p.hasPermission("cylrp.region.bypass"))
      return; 
    for (ProtectedRegion region : rgs) {
      if (region.getId().contains("vip") && !p.hasPermission("cylrp.rank.vip") && !p.hasPermission("cylrp.rank.vipplus") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'êtes pas vip pour accéder à cette région");
        return;
      } 
      if (region.getId().contains("vip+") && !p.hasPermission("cylrp.rank.vipplus") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cZVous n'êtes pas vip+ pour accéder à cette région");
        return;
      } 
      if (region.getId().contains("supervip") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cZVous n'êtes pas supervip pour accéder à cette région");
        return;
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    Player p = (Player)event.getPlayer();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    Location l = p.getLocation();
    Location lLoc = p.getEyeLocation();
    Jobs.Job[] jobs = Jobs.Job.values();
    Set<ProtectedRegion> rgs = RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(p.getLocation()).getRegions();
    if (p.hasPermission("cylrp.region.bypass"))
      return; 
    for (ProtectedRegion region : rgs) {
      if (region.getId().contains("vip") && !p.hasPermission("cylrp.rank.vip") && !p.hasPermission("cylrp.rank.vipplus") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'êtes pas vip pour accéder à cette région");
        return;
      } 
      if (region.getId().contains("vip+") && !p.hasPermission("cylrp.rank.vipplus") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'êtes pas vip+ pour accéder à cette région");
        return;
      } 
      if (region.getId().contains("supervip") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'êtes pas supervip pour accéder à cette région");
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
      if (region.getId().contains("vip") && !p.hasPermission("cylrp.rank.vip") && !p.hasPermission("cylrp.rank.vipplus") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cVous n'êtes pas vip pour accéder à cette région");
        return;
      } 
      if (region.getId().contains("vip+") && !p.hasPermission("cylrp.rank.vipplus") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cZVous n'êtes pas vip+ pour accéder à cette région");
        return;
      } 
      if (region.getId().contains("supervip") && !p.hasPermission("cylrp.rank.supervip")) {
        event.setCancelled(true);
        MessageUtils.sendMessage((CommandSender)p, "§cZVous n'êtes pas supervip pour accéder à cette région");
        return;
      } 
    } 
  }
}
