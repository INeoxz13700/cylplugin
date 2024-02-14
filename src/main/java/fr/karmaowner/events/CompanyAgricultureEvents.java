package fr.karmaowner.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyAgriculture;
import fr.karmaowner.companies.CompanyMinage;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.Permissions;
import fr.karmaowner.utils.RegionUtils;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.BlockCrops;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

public class CompanyAgricultureEvents implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockInteract(PlayerInteractEvent e) {

    if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
    {
      return;
    }

    Player p = e.getPlayer();

    PlayerData Player = PlayerData.getPlayerData(p.getName());

    RegionManager rgm = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName());
    LocalPlayer lp = Main.WG.wrapPlayer(p);
    Location l = e.getClickedBlock().getLocation();
    ApplicableRegionSet set = rgm.getApplicableRegions(l);
    Set<ProtectedRegion> rgs = set.getRegions();

    boolean isAgriculture = false;
    for (ProtectedRegion r : rgs) {
      if (r.getId().contains("champ")) {
        isAgriculture = true;
      }
    }

    if (!isAgriculture) return;


    CompanyData data = (CompanyData)CompanyData.Companies.get(Player.companyName);

    if (data == null || data.getCompany() == null || !(data.getCompany() instanceof CompanyAgriculture))
    {
      p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas d'entreprise d'agriculture.");
      e.setCancelled(true);
      return;
    }

    if (!CompanyAgriculture.isCompanyBlock(e.getClickedBlock())) {
      if (!p.hasPermission(Permissions.Staff)) {
        p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas casser ce bloc.");
        e.setCancelled(true);
      }
      return;
    }

      if (!(data.getCompany()).isThatRegion) {

        CompanyAgriculture ca = (CompanyAgriculture) data.getCompany();
        Material itemInHand = p.getItemInHand().getType();
        Block block = e.getClickedBlock();
        if (block != null) {

          Material brokenBlock = block.getType();
          Byte getdata = block.getData();
          if (brokenBlock != null) {
            Company.XP xp = ca.toXP(block.getTypeId());
            if (xp != null) {
              CropState c = CropState.getByData(getdata);
              if (c != null)
                if (ca.isItemUnlocked(xp)) {
                  if (c == CropState.RIPE || block.getData() == 3) {

                    if (itemInHand == Material.AIR) {
                      ca.addXp(e.getPlayer(), xp.getXp());
                    } else {
                      p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez récolter qu'avec les mains");
                      e.setCancelled(true);
                    }
                  }
                } else {
                  ca.locked_Message(p, xp);
                  e.setCancelled(true);
                }
            }
          }

        }

      }
  }
}
