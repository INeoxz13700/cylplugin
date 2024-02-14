package fr.karmaowner.jobs.parcelle.events;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.parcelle.Champ;
import fr.karmaowner.jobs.parcelle.Enclo;
import fr.karmaowner.jobs.parcelle.FreeArea;
import fr.karmaowner.jobs.parcelle.Local;
import fr.karmaowner.jobs.parcelle.Parcelle;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.RegionUtils;
import java.math.BigDecimal;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class ParcelleEvents implements Listener {
  @EventHandler
  public void inventoryClicked(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    ItemStack item = e.getCurrentItem();
    if ((e.isRightClick() || e.isLeftClick()) && 
      item != null && 
      item.getType() != Material.AIR)
      if (e.getClickedInventory().getName().equals(Local.getInv().getName())) {
        e.setCancelled(true);
        if (Local.isRegionExistByItemName(item.getItemMeta().getDisplayName())) {
          Parcelle pcelle = Local.getRegion(item);
          String region = pcelle.getName();
          ProtectedRegion r = RegionUtils.getRegionByName(region, "cyl");
          if (r != null) {
            CompanyData company = (CompanyData)CompanyData.Companies.get(data.companyName);
            if (!company.hasParcelle(region)) {
              if (data.getMoney().doubleValue() >= pcelle.getPrice()) {
                BigDecimal newMoney = data.getMoney().subtract(BigDecimal.valueOf(pcelle.getPrice()));
                data.setMoney(newMoney);
                double distanceX = r.getMaximumPoint().getX() - r.getMinimumPoint().getX();
                double distanceZ = r.getMaximumPoint().getZ() - r.getMinimumPoint().getZ();
                double distanceY = r.getMaximumPoint().getY() - r.getMinimumPoint().getY();
                FreeArea.initDot(Main.INSTANCE.getServer().getWorld("world1"));
                BlockVector min = FreeArea.getDot(r);
                BlockVector max = new BlockVector(min.getX() + distanceX, min.getY() + distanceY, min.getBlockZ() + distanceZ);
                UUID id = UUID.randomUUID();
                ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("Local_" + company.getCompanyName() + "_" + id, min, max);
                DefaultDomain gerant = new DefaultDomain();
                DefaultDomain members = new DefaultDomain();
                gerant.addPlayer(p.getName());
                for (String name : company.getUsersList())
                  members.addPlayer(name); 
                protectedCuboidRegion.copyFrom(r);
                protectedCuboidRegion.setMembers(members);
                protectedCuboidRegion.setOwners(gerant);
                RegionUtils.getRegionManager("world1").addRegion((ProtectedRegion)protectedCuboidRegion);
                p.sendMessage(ChatColor.GREEN + "Votre région est définie !");
                RegionUtils.putBlocksInRegion(Main.INSTANCE.getServer().getWorld("cyl"), Main.INSTANCE.getServer().getWorld("world1"), r, (ProtectedRegion)protectedCuboidRegion);
                p.sendMessage(ChatColor.GREEN + "Les blocs du local sont désormais placés !");
                company.setOwnedParcelle(region, protectedCuboidRegion.getId());
                p.sendMessage(ChatColor.GREEN + "Local créé avec succès !");
                p.closeInventory();
              } else {
                MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas assez d'argent");
              } 
            } else {
              MessageUtils.sendMessage((CommandSender)p, "Vous possédez déjà ce local");
            } 
          } else {
            p.sendMessage(ChatColor.DARK_RED + "Cette région n'a pas l'air d'exister.");
          } 
        } 
      } else if (e.getClickedInventory().getName().equals(Enclo.getInv().getName())) {
        e.setCancelled(true);
        if (Enclo.isRegionExistByItemName(item.getItemMeta().getDisplayName())) {
          Parcelle pcelle = Enclo.getRegion(item);
          String region = pcelle.getName();
          ProtectedRegion r = RegionUtils.getRegionByName(region, "cyl");
          if (r != null) {
            CompanyData company = (CompanyData)CompanyData.Companies.get(data.companyName);
            if (!company.hasParcelle(region)) {
              if (data.getMoney().doubleValue() >= pcelle.getPrice()) {
                BigDecimal newMoney = data.getMoney().subtract(BigDecimal.valueOf(pcelle.getPrice()));
                data.setMoney(newMoney);
                double distanceX = r.getMaximumPoint().getX() - r.getMinimumPoint().getX();
                double distanceZ = r.getMaximumPoint().getZ() - r.getMinimumPoint().getZ();
                double distanceY = r.getMaximumPoint().getY() - r.getMinimumPoint().getY();
                FreeArea.initDot(Main.INSTANCE.getServer().getWorld("world1"));
                BlockVector min = FreeArea.getDot(r);
                BlockVector max = new BlockVector(min.getX() + distanceX, min.getY() + distanceY, min.getBlockZ() + distanceZ);
                UUID id = UUID.randomUUID();
                ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("Enclos_" + company.getCompanyName() + "_" + id, min, max);
                DefaultDomain gerant = new DefaultDomain();
                DefaultDomain members = new DefaultDomain();
                gerant.addPlayer(p.getName());
                for (String name : company.getUsersList())
                  members.addPlayer(name); 
                protectedCuboidRegion.copyFrom(r);
                protectedCuboidRegion.setMembers(members);
                protectedCuboidRegion.setOwners(gerant);
                protectedCuboidRegion.setFlag((Flag)DefaultFlag.MOB_SPAWNING, StateFlag.State.ALLOW);
                RegionUtils.getRegionManager("world1").addRegion((ProtectedRegion)protectedCuboidRegion);
                p.sendMessage(ChatColor.GREEN + "Votre région est définie !");
                RegionUtils.putBlocksInRegion(Main.INSTANCE.getServer().getWorld("cyl"), Main.INSTANCE.getServer().getWorld("world1"), r, (ProtectedRegion)protectedCuboidRegion);
                p.sendMessage(ChatColor.GREEN + "Les blocs de l'enclos sont désormais placés !");
                company.setOwnedParcelle(region, protectedCuboidRegion.getId());
                p.sendMessage(ChatColor.GREEN + "Enclos crée avec succès !");
                p.closeInventory();
              } else {
                MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas assez d'argent");
              } 
            } else {
              MessageUtils.sendMessage((CommandSender)p, "Vous possédez déjà cet enclos");
            } 
          } else {
            p.sendMessage(ChatColor.DARK_RED + "Cette région n'a pas l'air d'exister.");
          } 
        } else {
          p.sendMessage(ChatColor.DARK_RED + "Cette région n'a pas l'air d'exister.");
        } 
      } else if (e.getClickedInventory().getName().equals(Champ.getInv().getName())) {
        e.setCancelled(true);
        if (Champ.isRegionExistByItemName(item.getItemMeta().getDisplayName())) {
          Parcelle pcelle = Champ.getRegion(item);
          String region = pcelle.getName();
          ProtectedRegion r = RegionUtils.getRegionByName(region, "cyl");
          if (r != null) {
            CompanyData company = (CompanyData)CompanyData.Companies.get(data.companyName);
            if (!company.hasParcelle(region)) {
              if (data.getMoney().doubleValue() >= pcelle.getPrice()) {
                BigDecimal newMoney = data.getMoney().subtract(BigDecimal.valueOf(pcelle.getPrice()));
                data.setMoney(newMoney);
                double distanceX = r.getMaximumPoint().getX() - r.getMinimumPoint().getX();
                double distanceZ = r.getMaximumPoint().getZ() - r.getMinimumPoint().getZ();
                double distanceY = r.getMaximumPoint().getY() - r.getMinimumPoint().getY();
                FreeArea.initDot(Main.INSTANCE.getServer().getWorld("world1"));
                BlockVector min = FreeArea.getDot(r);
                BlockVector max = new BlockVector(min.getX() + distanceX, min.getY() + distanceY, min.getBlockZ() + distanceZ);
                UUID id = UUID.randomUUID();
                ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("Champ_" + company.getCompanyName() + "_" + id, min, max);
                DefaultDomain gerant = new DefaultDomain();
                DefaultDomain members = new DefaultDomain();
                gerant.addPlayer(p.getName());
                for (String name : company.getUsersList())
                  members.addPlayer(name); 
                protectedCuboidRegion.copyFrom(r);
                protectedCuboidRegion.setMembers(members);
                protectedCuboidRegion.setOwners(gerant);
                RegionUtils.getRegionManager("world1").addRegion((ProtectedRegion)protectedCuboidRegion);
                p.sendMessage(ChatColor.GREEN + "Votre région est définie !");
                RegionUtils.putBlocksInRegion(Main.INSTANCE.getServer().getWorld("cyl"), Main.INSTANCE.getServer().getWorld("world1"), r, (ProtectedRegion)protectedCuboidRegion);
                p.sendMessage(ChatColor.GREEN + "Les blocs du Champ sont désormais placés !");
                company.setOwnedParcelle(region, protectedCuboidRegion.getId());
                p.sendMessage(ChatColor.GREEN + "Champ créé avec succès !");
                p.closeInventory();
              } else {
                MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas assez d'argent");
              } 
            } else {
              MessageUtils.sendMessage((CommandSender)p, "Vous possédez déjà ce champ");
            } 
          } else {
            p.sendMessage(ChatColor.DARK_RED + "Cette région n'a pas l'air d'exister.");
          } 
        } else {
          p.sendMessage(ChatColor.DARK_RED + "Cette région n'a pas l'air d'exister.");
        } 
      }  
  }
  
  @EventHandler
  public void onEnterRegionParcelle(PlayerMoveEvent e) {
    Location to = e.getTo();
    Player p = e.getPlayer();
    World world = p.getWorld();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.companyName != null) {
      CompanyData company = (CompanyData)CompanyData.Companies.get(data.companyName);
      if (company != null && 
        !company.getParcelles().isEmpty())
        for (ProtectedRegion r : RegionUtils.getRegionManager(world.getName()).getApplicableRegions(to)) {
          if (company.isParcelle(r.getId())) {
            String ownedParcelle = company.getOwnedParcelleOut(r.getId());
            ProtectedRegion owned = RegionUtils.getRegionManager("world1").getRegion(ownedParcelle);
            p.teleport(RegionUtils.getLocation(Main.INSTANCE.getServer().getWorld("world1"), RegionUtils.getMiddleXZ(Main.INSTANCE.getServer().getWorld("world1"), owned)));
            p.sendMessage(ChatColor.GREEN + "Vous venez d'être téléporté dans le local de votre entreprise !");
            return;
          } 
        }  
    } 
  }
  
  @EventHandler
  public void onQuitRegionParcelleOwned(PlayerMoveEvent e) {
    Location to = e.getTo();
    Location from = e.getFrom();
    Player p = e.getPlayer();
    World world = p.getWorld();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.companyName != null) {
      CompanyData company = (CompanyData)CompanyData.Companies.get(data.companyName);
      if (company != null && 
        !company.getOwnedParcelle().isEmpty())
        for (ProtectedRegion r : RegionUtils.getRegionManager(world.getName()).getApplicableRegions(from)) {
          if (company.isOwnedParcelle(r.getId())) {
            for (ProtectedRegion r2 : RegionUtils.getRegionManager(world.getName()).getApplicableRegions(to)) {
              if (r2.getId().equals(r.getId()))
                return; 
            } 
            String Parcelle = company.getOwnedParcelleIn(r.getId());
            ProtectedRegion pcl = RegionUtils.getRegionManager("cyl").getRegion(Parcelle);
            Location middle = RegionUtils.getLocation(Main.INSTANCE.getServer().getWorld("cyl"), RegionUtils.getMiddleXZ(Main.INSTANCE.getServer().getWorld("cyl"), pcl));
            Location teleportLocation = RegionUtils.getLocationAboveRegion(Main.INSTANCE.getServer().getWorld("cyl"), pcl, middle, 5);
            double y = RegionUtils.getTopBlockAtXZ(Main.INSTANCE.getServer().getWorld("cyl"), teleportLocation.getX(), teleportLocation.getZ());
            p.teleport(new Location(Main.INSTANCE.getServer().getWorld("cyl"), teleportLocation.getX(), y, teleportLocation.getZ()));
            p.sendMessage(ChatColor.GREEN + "Vous venez d'être téléporté dans la ville principale !");
            return;
          } 
        }  
    } 
  }
}
