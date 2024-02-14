package fr.karmaowner.events;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.utils.RegionUtils;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionEvents implements Listener {
  @EventHandler(priority = EventPriority.LOWEST)
  public void onExplosion(EntityExplodeEvent e) {
    /*Location explosionLocation = e.getLocation();
    RegionManager rg = RegionUtils.getRegionManager("cyl");
    if (rg != null) {
      Set<ProtectedRegion> rgs = rg.getApplicableRegions(explosionLocation).getRegions();
      for (ProtectedRegion region : rgs) {
        if (region.getId().contains("bank-explosion"))
          return; 
      } 
      e.setCancelled(true);
    }*/
  }
}
