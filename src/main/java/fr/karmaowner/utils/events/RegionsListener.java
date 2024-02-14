package fr.karmaowner.utils.events;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.RegionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RegionsListener implements Listener {
  @EventHandler
  public void onRegionEnter(PlayerMoveEvent e) {
    ApplicableRegionSet ars = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getFrom());
    ApplicableRegionSet ars2 = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getTo());
    if (ars.getRegions().isEmpty()) {
      if (!ars2.getRegions().isEmpty())
        Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionEnterEvent(ars2.getRegions(), e.getPlayer())); 
    } else if (!ars.getRegions().isEmpty() && !ars2.getRegions().isEmpty()) {
      for (ProtectedRegion rg : ars.getRegions()) {
        for (ProtectedRegion rg2 : ars2.getRegions()) {
          if (rg.getId().equalsIgnoreCase(rg2.getId()))
            return; 
        } 
      } 
      Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionEnterEvent(ars2.getRegions(), e.getPlayer()));
    } 
  }
  
  @EventHandler
  public void onRegionEnter2(PlayerTeleportEvent e) {
    ApplicableRegionSet ars = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getFrom());
    ApplicableRegionSet ars2 = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getTo());
    if (ars.getRegions().isEmpty()) {
      if (!ars2.getRegions().isEmpty())
        Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionEnterEvent(ars2.getRegions(), e.getPlayer())); 
    } else if (!ars.getRegions().isEmpty() && !ars2.getRegions().isEmpty()) {
      for (ProtectedRegion rg : ars.getRegions()) {
        for (ProtectedRegion rg2 : ars2.getRegions()) {
          if (rg.getId().equalsIgnoreCase(rg2.getId()))
            return; 
        } 
      } 
      Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionEnterEvent(ars2.getRegions(), e.getPlayer()));
    } 
  }
  
  @EventHandler
  public void onRegionQuit(PlayerMoveEvent e) {
    ApplicableRegionSet ars = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getFrom());
    if (!ars.getRegions().isEmpty()) {
      ApplicableRegionSet ars2 = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getTo());
      if (ars2.getRegions().isEmpty()) {
        Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionQuitEvent(ars.getRegions(), e.getPlayer()));
      } else {
        for (ProtectedRegion rg : ars2.getRegions()) {
          for (ProtectedRegion rg2 : ars.getRegions()) {
            if (rg.getId().equalsIgnoreCase(rg2.getId()))
              return; 
          } 
        } 
        Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionQuitEvent(ars.getRegions(), e.getPlayer()));
      } 
    } 
  }
  
  @EventHandler
  public void onRegionQuit2(PlayerTeleportEvent e) {
    ApplicableRegionSet ars = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getFrom());
    if (!ars.getRegions().isEmpty()) {
      ApplicableRegionSet ars2 = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName()).getApplicableRegions(e.getTo());
      if (ars2.getRegions().isEmpty()) {
        Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionQuitEvent(ars.getRegions(), e.getPlayer()));
      } else {
        for (ProtectedRegion rg : ars2.getRegions()) {
          for (ProtectedRegion rg2 : ars.getRegions()) {
            if (rg.getId().equalsIgnoreCase(rg2.getId()))
              return; 
          } 
        } 
        Main.INSTANCE.getServer().getPluginManager().callEvent(new RegionQuitEvent(ars.getRegions(), e.getPlayer()));
      } 
    } 
  }
}
