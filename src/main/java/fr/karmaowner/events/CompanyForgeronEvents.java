package fr.karmaowner.events;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyForgeron;
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
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class CompanyForgeronEvents implements Listener {

    @EventHandler
    public void transformItems(InventoryDragEvent e) {

        if(e.getInventory().getType() != InventoryType.FURNACE)
        {
            return;
        }

        if(e.getOldCursor() == null || e.getOldCursor().getType() == Material.AIR)
        {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        PlayerData Player = PlayerData.getPlayerData(p.getName());

        RegionManager rgm = RegionUtils.getRegionManager(p.getWorld().getName());
        LocalPlayer lp = Main.WG.wrapPlayer(p);
        Location l = p.getLocation();
        ApplicableRegionSet set = rgm.getApplicableRegions(l);
        Set<ProtectedRegion> rgs = set.getRegions();
        boolean isForge = false;
        for (ProtectedRegion r : rgs) {
            if(r.getId().contains("forgeron")) {
                isForge = true;
            }
        }

        if(!isForge)
        {

            if(CompanyForgeron.isCompanyBlock(e.getOldCursor()))
            {
                e.setCancelled(true);
                p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas forger ce bloc ici.");
            }
            return;

        }


        CompanyData company = CompanyData.Companies.get(Player.companyName);

        if (company == null || company.getCompany() == null || !(company.getCompany() instanceof CompanyForgeron))
        {
            p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas d'entreprise de forgeron.");
            e.setCancelled(true);
            return;
        }

        if (!(company.getCompany()).isThatRegion) {

                if (e.getInventory().getType() == InventoryType.FURNACE) {

                    CompanyForgeron cf = (CompanyForgeron) company.getCompany();

                    ItemStack furnaceBurningItem = e.getOldCursor();

                    Material r = furnaceBurningItem.getType();
                    byte data = furnaceBurningItem.getData().getData();

                    Company.XP item = cf.toXP(r, data);
                    if (item == null) {
                        item = cf.toXP(furnaceBurningItem.getTypeId(), data);
                    }
                    CompanyForgeron.XP_FORGERON xf = (CompanyForgeron.XP_FORGERON) item;

                    if (r != null) {
                        if (item != null) {
                            if(!cf.isItemUnlocked(item))
                            {
                                cf.locked_Message(p, item);
                                e.setCancelled(true);
                            }
                        }
                        else
                        {
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.DARK_RED + "Cette objet n'est pas farmable.");
                        }

                    }
                }
        }
    }

    @EventHandler
    public void transformItems(InventoryClickEvent e) {

        if(e.getInventory().getType() != InventoryType.FURNACE)
        {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        PlayerData Player = PlayerData.getPlayerData(p.getName());



        RegionManager rgm = RegionUtils.getRegionManager(p.getWorld().getName());
        LocalPlayer lp = Main.WG.wrapPlayer(p);
        Location l = p.getLocation();
        ApplicableRegionSet set = rgm.getApplicableRegions(l);
        Set<ProtectedRegion> rgs = set.getRegions();
        boolean isForge = false;
        for (ProtectedRegion r : rgs) {
            if(r.getId().contains("forgeron")) {
                isForge = true;
            }
        }

        if(!isForge)
        {
            if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                p.sendMessage(ChatColor.DARK_RED + "Les raccourcis ne sont pas autorisés.");
                e.setCancelled(true);
            } else if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                p.sendMessage(ChatColor.DARK_RED + "Les raccourcis ne sont pas autorisés.");
                e.setCancelled(true);
            }
            else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                p.sendMessage(ChatColor.DARK_RED + "Les raccourcis ne sont pas autorisés.");
                e.setCancelled(true);
            }

            if(e.getClickedInventory().getType() == InventoryType.FURNACE && e.getSlot() == 0)
            {
                if(CompanyForgeron.isCompanyBlock(e.getCursor()))
                {
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas forger ce bloc ici.");
                }
            }
            return;
        }

        CompanyData company = CompanyData.Companies.get(Player.companyName);

        if (company == null || company.getCompany() == null || !(company.getCompany() instanceof CompanyForgeron))
        {
            p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas d'entreprise de forgeron.");
            e.setCancelled(true);
            return;
        }


        if (!(company.getCompany()).isThatRegion) {
            if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                p.sendMessage(ChatColor.DARK_RED + "Les raccourcis ne sont pas autorisés.");
                e.setCancelled(true);
            } else if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                p.sendMessage(ChatColor.DARK_RED + "Les raccourcis ne sont pas autorisés.");
                e.setCancelled(true);
            }
            else if (e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                p.sendMessage(ChatColor.DARK_RED + "Les raccourcis ne sont pas autorisés.");
                e.setCancelled(true);
            }


            CompanyForgeron cf = (CompanyForgeron) company.getCompany();


            if (e.getClickedInventory().getType() == InventoryType.FURNACE && e.getSlot() == 0) {
                if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    ItemStack furnaceBurningItem = e.getCursor();

                    Material r = furnaceBurningItem.getType();
                    byte data = furnaceBurningItem.getData().getData();

                    Company.XP item = cf.toXP(r, data);
                    if (item == null) {
                        item = cf.toXP(furnaceBurningItem.getTypeId(), data);
                    }
                    CompanyForgeron.XP_FORGERON xf = (CompanyForgeron.XP_FORGERON) item;

                    if (r != null) {
                        if (item != null) {
                            if (!cf.isItemUnlocked(item)) {
                                cf.locked_Message(p, item);
                                e.setCancelled(true);
                            }
                        } else {
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.DARK_RED + "Cette objet n'est pas farmable.");
                        }

                    }
                }
            }
            else if (e.getClickedInventory().getType() == InventoryType.FURNACE && e.getSlot() == 2)
            {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    ItemStack furnaceBurningItem = e.getCurrentItem();

                    Material r = furnaceBurningItem.getType();
                    byte data = furnaceBurningItem.getData().getData();

                    Company.XP item = cf.toXP(r, data);
                    if (item == null) {
                        item = cf.toXP(furnaceBurningItem.getTypeId(), data);
                    }

                    CompanyForgeron.XP_FORGERON xf = (CompanyForgeron.XP_FORGERON) item;

                    if (r != null) {

                        if (item != null) {
                            if (cf.isItemUnlocked(item)) {
                                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                    for (int i = 0; i < e.getCurrentItem().getAmount(); i++) {
                                        cf.addXp(p, item);
                                    }
                                    cf.setCompanyAchievements();
                                }
                            }
                        } else {
                            e.setCancelled(true);
                            p.sendMessage(ChatColor.DARK_RED + "Cette objet n'est pas farmable.");
                        }

                    }
                }

            }


        }
    }



    @EventHandler
    public void removeXpFromItem(FurnaceExtractEvent e) {
        e.setExpToDrop(0);
    }
}
