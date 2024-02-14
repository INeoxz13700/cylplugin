package fr.karmaowner.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyMenuiserie;
import fr.karmaowner.companies.CompanyMetallurgie;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Cuisinier;
import fr.karmaowner.utils.RegionUtils;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;

public class CompanyMenuiserieEvents implements Listener {
  @EventHandler
  public void CraftingItems(CraftItemEvent e) {
    Player p = (Player)e.getWhoClicked();

    PlayerData Player = PlayerData.getPlayerData(p.getName());

    if(Player.selectedJob instanceof Cuisinier && e.getRecipe().getResult() != null && Cuisinier.CraftItems.isAbleToCraft(e.getRecipe().getResult())) {
      return;
    }

    if(Player.hasCompany())
    {
      CompanyData company = CompanyData.Companies.get(Player.companyName);
      if(company.getCompany() instanceof CompanyMetallurgie) {
        CompanyMetallurgie cm = (CompanyMetallurgie) company.getCompany();
        Material material = e.getRecipe().getResult().getType();
        int id = e.getRecipe().getResult().getTypeId();
        byte metaData = e.getRecipe().getResult().getData().getData();
        Company.XP item = cm.toXP(material, metaData);
        item = (item == null) ? cm.toXP(id, metaData) : item;
        if (item != null) {
          return;
        }
      }
      else if (company.getCompany() instanceof CompanyMenuiserie) {
        if (!(company.getCompany()).isThatRegion) {
          RegionManager rgm = RegionUtils.getRegionManager(p.getWorld().getName());
          LocalPlayer lp = Main.WG.wrapPlayer(p);
          Location l = p.getLocation();
          ApplicableRegionSet set = rgm.getApplicableRegions(l);
          Set<ProtectedRegion> rgs = set.getRegions();
          for (ProtectedRegion r : rgs) {
            if (!r.getId().contains("menuiserie")) {
              p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas fabriquer d'objets ici");
              e.setCancelled(true);
              return;
            }
          }

          if(rgs.isEmpty())
          {
            p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas fabriquer d'objets ici");
            e.setCancelled(true);
            return;
          }

          if (e.getClickedInventory().getType() != InventoryType.CRAFTING) {
            CompanyMenuiserie cm = (CompanyMenuiserie) company.getCompany();
            Material r = e.getRecipe().getResult().getType();
            int id = e.getRecipe().getResult().getTypeId();
            byte data = e.getRecipe().getResult().getData().getData();
            Company.XP item = cm.toXP(r, data);
            item = (item == null) ? cm.toXP(id, data) : item;
            if (item != null) {
              if (cm.isItemUnlocked(item)) {
                if (e.getAction().equals(InventoryAction.PICKUP_ALL) || e.getAction().equals(InventoryAction.PICKUP_HALF) || e.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                  cm.addXp(p, item);
                  cm.setCompanyAchievements();
                } else if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                  e.setCancelled(true);
                }
              } else {
                cm.locked_Message(p, item);
                e.setCancelled(true);
              }
            } else {
              e.setCancelled(true);
              p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas fabriquer cet objet.");
            }
          }
        } else {
          e.setCancelled(true);
          p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas fabriquer d'objets chez vous");
        }
      }
    }
  }
}
