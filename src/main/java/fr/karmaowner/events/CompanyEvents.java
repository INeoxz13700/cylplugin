package fr.karmaowner.events;

import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.companies.pluginevent.CompanyGainXpEvent;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.RegionUtils;
import fr.karmaowner.utils.ServerUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;

public class CompanyEvents implements Listener {
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    if ((PlayerData.getPlayerData(e.getPlayer().getName())).companyName != null) {
      CompanyData cd = (CompanyData)CompanyData.Companies.get((PlayerData.getPlayerData(e.getPlayer().getName())).companyName);
      if (cd != null && 
        cd.getCompany() != null) {
        (cd.getCompany()).isThatRegion = false;
        for (ProtectedRegion r : Main.WG.getRegionManager(e.getPlayer().getWorld()).getApplicableRegions(e.getTo())) {
          if (!r.getId().contains("champ") && !r.getId().contains("enclos")) {
            LocalPlayer lp = Main.WG.wrapPlayer(e.getPlayer());
            if (r.isMember(lp) || r.isOwner(lp)) {
              (cd.getCompany()).isThatRegion = true;
              return;
            } 
          }
        } 
      } 
    } 
  }
  
  @EventHandler
  public void agreeOpenCraftingTable(InventoryOpenEvent e) {
    Player p = (Player)e.getPlayer();
    Inventory inv = e.getInventory();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    if (pData == null)
      return; 
    CompanyData company = (CompanyData)CompanyData.Companies.get(pData.companyName);
    if (!p.hasPermission("cylrp.admin")) {
      if ((pData.selectedJob instanceof fr.karmaowner.jobs.Cuisinier) || (company != null && (company.getCompany() instanceof fr.karmaowner.companies.CompanyMenuiserie || company.getCompany() instanceof fr.karmaowner.companies.CompanyMetallurgie)))
        return; 
      if (inv.getType().equals(InventoryType.WORKBENCH)) {
        p.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas autorisé à ouvrir la table de craft.");
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler
  public void clickOnPlayerCraftingTable(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();

    if (!p.hasPermission("cylrp.admin"))
    {
      if(e.getClickedInventory() != null && e.getClickedInventory().getType().equals(InventoryType.CRAFTING) && (
       e.getSlot() == 0 || e.getSlot() == 1 || e
        .getSlot() == 2 || e.getSlot() == 3 || e.getSlot() == 4))
      {
        p.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas autorisé à faire cela.");
        e.setCancelled(true);
      }
      else if(ServerUtils.isPlayerInventory(e.getClickedInventory()))
      {
        if(e.getSlot() == 0)
        {
          e.setCancelled(true);
        }
      }
    }
  }


  @EventHandler
  public void AgreeOpenFurnaceTable(InventoryOpenEvent e) {
    Player p = (Player)e.getPlayer();
    Inventory inv = e.getInventory();
    PlayerData Player = PlayerData.getPlayerData(p.getName());
    CompanyData company = (CompanyData)CompanyData.Companies.get(Player.companyName);
    if (!p.hasPermission("cylrp.admin")) {
      if (company != null)
        if (!(company.getCompany() instanceof fr.karmaowner.companies.CompanyForgeron)) {
          if (inv.getType().equals(InventoryType.FURNACE)) {
            if (Player.selectedJob instanceof fr.karmaowner.jobs.Cuisinier)
              return; 
            p.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas autorisé à ouvrir le four.");
            e.setCancelled(true);
          } 
        } else {
          if (inv.getType().equals(InventoryType.FURNACE) && 
            e.getViewers().size() > 1) {
            p.sendMessage(ChatColor.DARK_RED + "Quelqu'un est déjà en train d'utiliser ce four. Attendez votre tour...");
            e.setCancelled(true);
          } 
          return;
        }  
      if (Player.selectedJob != null && !(Player.selectedJob instanceof fr.karmaowner.jobs.Cuisinier)) {
        if (inv.getType().equals(InventoryType.FURNACE)) {
          p.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas autorisé à ouvrir le four.");
          e.setCancelled(true);
        } 
      } else if (inv.getType().equals(InventoryType.FURNACE) && 
        e.getViewers().size() > 1) {
        p.sendMessage(ChatColor.DARK_RED + "Quelqu'un est déjà en train d'utiliser ce four. Attendez votre tour...");
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void OnBlockBreak(BlockBreakEvent e) {
    RegionManager rgm = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName());
    Player p = e.getPlayer();
    LocalPlayer lp = Main.WG.wrapPlayer(p);
    Location l = e.getBlock().getLocation();
    ApplicableRegionSet set = rgm.getApplicableRegions(l);
    if (!set.canBuild(lp) && !p.hasPermission("cylrp.region.bypass.build"))
      e.setCancelled(true); 
  }

  @EventHandler
  public void onCompanyGainXp(CompanyGainXpEvent event)
  {
    Player p = event.xpOwner;
    double currentXP = event.company.getXp() + event.xpGain;
    double nextLvlXP = event.company.getXpToReachForLevelUp();
    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cProgression de votre entreprise : " + currentXP + "XP / " + nextLvlXP + "XP"));
  }

}
