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
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.Permissions;
import fr.karmaowner.utils.RegionUtils;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CompanyMinageEvents implements Listener {
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void BlockBreakEvent(BlockBreakEvent e) {
    Player p = e.getPlayer();
    Block brokenBlock = e.getBlock();
    PlayerData Player = PlayerData.getPlayerData(p.getName());

    RegionManager rgm = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName());
    LocalPlayer lp = Main.WG.wrapPlayer(p);
    Location l = e.getBlock().getLocation();
    ApplicableRegionSet set = rgm.getApplicableRegions(l);
    Set<ProtectedRegion> rgs = set.getRegions();

    boolean isMine = false;
    for (ProtectedRegion r : rgs) {
        if(r.getId().contains("mine")) {
            isMine = true;
        }
    }

    if(!isMine)
    {
        if(!p.hasPermission(Permissions.Staff))
        {
            if(CompanyMinage.isCompanyBlock(brokenBlock))
            {
                e.setCancelled(true);
                e.setDropItems(false);
            }
        }
        return;
    }

    CompanyData data = (CompanyData)CompanyData.Companies.get(Player.companyName);

    if (data == null || data.getCompany() == null || !(data.getCompany() instanceof CompanyMinage))
    {
          p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas d'entreprise de minage.");
          e.setCancelled(true);
          return;
    }

    if(!CompanyMinage.isCompanyBlock(brokenBlock))
    {
        if (!p.hasPermission(Permissions.Staff)) {
            p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas casser ce bloc.");
            e.setCancelled(true);
        }
        return;
    }


    Material itemInHand = p.getItemInHand().getType();

    if (!(data.getCompany()).isThatRegion)
    {
        CompanyMinage cm = (CompanyMinage)data.getCompany();
        Company.XP itemInHandXP = cm.toXP(itemInHand);
        Company.XP brokenBlockXP = cm.toXP(brokenBlock.getType());
        if(brokenBlockXP == null) brokenBlockXP = cm.toXP(brokenBlock.getTypeId(),(byte)0);

        if(brokenBlockXP == null) {
          return;
   }

   CompanyMinage.XP_MINAGE mxp = (CompanyMinage.XP_MINAGE)brokenBlockXP;
   if (cm.isItemUnlocked(itemInHandXP) && cm.isPickaxe(itemInHand)) {

       if (brokenBlockXP != null) {
           if (cm.isItemUnlocked(brokenBlockXP)) {

               cm.addXp(p, brokenBlockXP);
                cm.setCompanyAchievements();
                if (mxp.getType() == null) {
                    p.getInventory().addItem(ItemUtils.getItem(mxp.getItemBlockId(), (byte)0, 1, null, null));
                } else {
                    p.getInventory().addItem(ItemUtils.getItem(mxp.getType().getId(), mxp.getData(), 1, null, null));
                }
                e.setExpToDrop(0);
                e.getBlock().setType(Material.AIR);
            } else {
              cm.locked_Message(p, brokenBlockXP);
              e.setCancelled(true);
            } 
          } else {
            p.sendMessage("Ce bloc n'est pas cassable.");
            e.setCancelled(true);
          } 
        } else if (brokenBlockXP != null && !cm.isPickaxe(itemInHand)) {
          p.sendMessage(ChatColor.RED + "Vous ne pouvez pas casser ce bloc sans pioche");
          e.setCancelled(true);
        } else if (cm.isPickaxe(itemInHand) && !cm.isItemUnlocked(itemInHandXP)) {
          cm.locked_Message(p, itemInHandXP);
          e.setCancelled(true);
        }
        else if(cm.isPickaxe(itemInHand))
        {
            p.sendMessage(ChatColor.RED + "Cette pioche n'est pas utilisable");
            e.setCancelled(true);
        }
      }
  }
}
