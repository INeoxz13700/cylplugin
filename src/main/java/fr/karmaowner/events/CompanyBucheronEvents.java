package fr.karmaowner.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyAgriculture;
import fr.karmaowner.companies.CompanyBucheron;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
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

public class CompanyBucheronEvents implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void BlockBreakEvent(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block brokenBlock = e.getBlock();
        Material itemInHand = p.getItemInHand().getType();

        RegionManager rgm = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName());
        LocalPlayer lp = Main.WG.wrapPlayer(p);
        Location l = e.getBlock().getLocation();
        ApplicableRegionSet set = rgm.getApplicableRegions(l);
        Set<ProtectedRegion> rgs = set.getRegions();


        boolean isForest = false;
        for (ProtectedRegion r : rgs) {
            if (r.getId().contains("bucheron")) {
                isForest = true;
            }
        }

        if (!isForest) return;


        CompanyData data = CompanyData.Companies.get((PlayerData.getPlayerData(p.getName())).companyName);


        if (data == null || data.getCompany() == null || !(data.getCompany() instanceof CompanyBucheron))
        {
            p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas d'entreprise de bucheron.");
            e.setCancelled(true);
            return;
        }

        if (!CompanyBucheron.isCompanyBlock(e.getBlock())) {
            if (!p.hasPermission(Permissions.Staff)) {
                p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas casser ce bloc.");
                e.setCancelled(true);
            }
            return;
        }

        if (!(data.getCompany()).isThatRegion) {

            CompanyBucheron cb = (CompanyBucheron) data.getCompany();
            Company.XP brokenBlockXP = cb.toXP(brokenBlock.getType(), (byte)(brokenBlock.getData() & 0x03) );
            if(brokenBlockXP == null)
            {
                brokenBlockXP = cb.toXP(brokenBlock.getTypeId(), (byte)(brokenBlock.getData() & 0x03));
            }
            Company.XP itemInHandXP = cb.toXP(itemInHand);

            if (itemInHandXP != null && cb.isAxe(itemInHand)) {

                if (cb.isItemUnlocked(itemInHandXP)) {


                    if (brokenBlockXP != null) {
                        if(cb.isItemUnlocked(brokenBlockXP))
                        {
                            cb.addXp(p, brokenBlockXP);
                            cb.setCompanyAchievements();
                        }
                        else
                        {
                            cb.locked_Message(p, brokenBlockXP);
                            e.setCancelled(true);
                        }
                    } else {
                        e.setCancelled(true);
                    }
                } else {

                    cb.locked_Message(p, itemInHandXP);
                    e.setCancelled(true);
                }
            } else {
                p.sendMessage("§cVous ne pouvez effectuer cette action sans hache ou cette hache n'est pas disponible.");
                e.setCancelled(true);
            }

        }

    }
}
