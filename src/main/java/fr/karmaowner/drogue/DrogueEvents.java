package fr.karmaowner.drogue;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DrogueEvents implements Listener {
  /*@EventHandler
  public void onBreakDrug(BlockBreakEvent e) {
    Player p = e.getPlayer();


    Location location = e.getBlock().getLocation();

    if (p.hasPermission("cylrp.drogue")) return;

    ProtectedRegion choose = null;
    for (ProtectedRegion rg : RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(location).getRegions()) {
      for (ProtectedRegion rg2 : Drogue.REGIONS) {
        if (rg.getId().toLowerCase().equals(rg2.getId().toLowerCase())) {
          choose = rg;
          break;
        } 
      } 
      if (choose != null)
        break; 
    }

    if (choose != null &&
      !choose.getId().equals(Drogue.choosenRg.getId())) {
      e.setCancelled(true);
      p.sendMessage(ChatColor.RED + "Cette région est indisponible pour la drogue.");
    } 
  }*/
}
