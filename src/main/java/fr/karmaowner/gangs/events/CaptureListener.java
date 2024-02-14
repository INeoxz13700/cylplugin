package fr.karmaowner.gangs.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.gangs.Capture;
import fr.karmaowner.utils.RegionUtils;
import fr.karmaowner.utils.events.RegionEnterEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CaptureListener implements Listener {
  @EventHandler
  public void blocInteractEvent(PlayerInteractEvent e) {
    Block b = e.getClickedBlock();
    Player p = e.getPlayer();
    PlayerData data = PlayerData.getPlayerData(p.getName());

    if (p.hasPermission("cylrp.gang.bypassregion")) return;

    if (b != null && b.getTypeId() == 54)
    {
      Capture c = null;
      for (ProtectedRegion rg : RegionUtils.getRegionManager(p.getWorld().getName()).getApplicableRegions(b.getLocation()).getRegions())
      {
        c = Capture.getCapture(rg.getId());
        if (c != null) break;
      } 
      if (c != null)
      {
        if (data.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste && data.gangName != null && !data.gangName.isEmpty())
        {
          GangData gData = GangData.getGang(data.gangName);
          if (c.getCaptureOwner() == null || !c.getCaptureOwner().equals(gData.getGangName()))
          {
            p.sendMessage(ChatColor.RED + "Cette région ne vous appartient pas. Vous ne pouvez pas ouvrir les coffres.");
            e.setCancelled(true);
          }
          if (!(data.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste))
          {
            e.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas intéragir avec les coffres car votre métier ne vous le permet pas.");
            e.setCancelled(true);
          }
        }
        else
        {
          if (c.getCaptureOwner() == null && data.selectedJob instanceof fr.karmaowner.jobs.Militaire) return;

          p.sendMessage(ChatColor.RED + "Vous ne pouvez pas accéder aux coffres de cette région.");
          e.setCancelled(true);
        }
      }
    }

  }
  
  @EventHandler
  public void OnRegionEnter(RegionEnterEvent e) {
    for (ProtectedRegion rg : e.getRegions()) {
      Capture c = Capture.getCapture(rg.getId());
      if (c != null)
      {
        if (c.getCaptureOwner() != null)
        {
          e.getPlayer().sendMessage("§9Zone sous l'autorité du gang §1" + c.getCaptureOwner());
          continue;
        } 
        e.getPlayer().sendMessage("§aZone sous la dominance des §2militaires");
      } 
    } 
  }
}
