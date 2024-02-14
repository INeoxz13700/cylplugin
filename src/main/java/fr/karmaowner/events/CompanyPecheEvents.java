package fr.karmaowner.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyPeche;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.RegionUtils;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class CompanyPecheEvents implements Listener {
  @EventHandler
  public void PlayerFish(PlayerFishEvent e) {
    Player p = e.getPlayer();
    PlayerData Player = PlayerData.getPlayerData(p.getName());
    if (Player == null || Player.companyName == null)
      return; 
    CompanyData company = (CompanyData)CompanyData.Companies.get(Player.companyName);
    if (company != null) {
      if (company.getCompany() instanceof CompanyPeche) {
        if (!(company.getCompany()).isThatRegion) {
          if (e.getCaught() != null) {
            Location l = e.getCaught().getLocation();
            RegionManager rgm = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName());
            LocalPlayer lp = Main.WG.wrapPlayer(p);
            ApplicableRegionSet set = rgm.getApplicableRegions(l);
            Set<ProtectedRegion> rgs = set.getRegions();
            for (ProtectedRegion r : rgs) {
              if (r.isMember(lp) || r.isOwner(lp)) {
                p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas pêcher à cet endroit");
                e.setCancelled(true);
                return;
              } 
            } 
          }

          CompanyPeche cp = (CompanyPeche)company.getCompany();
          if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Material mat = ((Item)e.getCaught()).getItemStack().getType();
            byte data = ((Item)e.getCaught()).getItemStack().getData().getData();
            Company.XP item = cp.toXP(mat, data);
            if(item == null) item = cp.toXP(((Item)e.getCaught()).getItemStack().getTypeId(), data);


            if(item == null)
            {
              e.setCancelled(true);
              p.sendMessage(ChatColor.RED + "Pas de chance, l'hameçon a été tiré mais il n'y a rien à pêcher.");
              return;
            }

            if (cp.isItemUnlocked(item)) {

              cp.addXp(p, item);
              cp.setCompanyAchievements();
            } else {
              cp.locked_Message(p, item);
              e.setCancelled(true);
            } 
          } else if (e.getState() == PlayerFishEvent.State.FISHING) {
            p.sendMessage(ChatColor.GOLD + "Pêche en cours...");
          } 
        } else {
          e.setCancelled(true);
        } 
      } else {
        e.setCancelled(true);
      } 
    } else {
      e.setCancelled(true);
    } 
  }
}
