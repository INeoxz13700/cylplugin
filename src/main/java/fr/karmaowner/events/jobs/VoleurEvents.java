package fr.karmaowner.events.jobs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Voleur;
import fr.karmaowner.jobs.voleur.Caisse;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.RandomItem;
import fr.karmaowner.utils.TimerUtils;
import fr.karmaowner.wantedlist.WantedList;
import java.sql.Timestamp;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class VoleurEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Location ploc = p.getLocation();
    final Vector direction = p.getEyeLocation().getDirection();
    if (inventory.getName().equals(Jobs.NAMEACTIONINVENTORY)) {
      if (item != null && 
        item.getType() != Material.AIR && 
        data.selectedJob instanceof Voleur) {
        final Voleur h = (Voleur)data.selectedJob;
        Voleur.Action a = Voleur.Action.VOLER1;
        Voleur.Action b = Voleur.Action.VOLER2;
        Voleur.Action c = Voleur.Action.VOLER3;
        Voleur.Action d = Voleur.Action.VOLER4;
        Voleur.Action f = Voleur.Action.VOLER5;
        if (item.getItemMeta().getDisplayName().equals(a.getDisplayName())) {
          if (h.getTask() == null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - h.getTimestamp().getTime() >= 120000L) {
              Set<ProtectedRegion> rgs = Main.WG.getRegionManager(p.getWorld()).getApplicableRegions(h.getTarget().getLocation()).getRegions();
              if (rgs.size() > 0) {
                p.sendMessage(ChatColor.RED + "Vous ne pouvez pas braquer de joueurs dans des régions !");
                return;
              } 
              h.setTask(new TaskCreator(new CustomRunnable() {
                      private int time = 0;
                      
                      public void customRun() {
                        if (h.getTarget().getLocation().distance(p.getLocation()) > 2.0D) {
                          p.sendMessage(ChatColor.RED + "Action annulée ! Rapprochez-vous de la cible !");
                          h.setTask(null);
                          cancel();
                        } 
                        if (this.time == 0)
                          p.sendMessage(ChatColor.DARK_AQUA + "Fouille en cours..."); 
                        if (this.time == 8)
                          p.sendMessage(ChatColor.DARK_AQUA + "Recherche d'objet rare en cours..."); 
                        if (this.time == 18) {
                          p.sendMessage(ChatColor.DARK_AQUA + "Objet trouvé");
                          p.getInventory().addItem(h.getRandomize().getItem());
                          h.setTimestamp();
                          h.setTask(null);
                          cancel();
                        } 
                        this.time++;
                      }
                    },  false, 0L, 20L));
            } else {
              long timeLeft = 120000L - (now.getTime() - h.getTimestamp().getTime());
              p.sendMessage(ChatColor.RED + "Vous devez attendre " + ChatColor.DARK_RED + TimerUtils.formatString((int)(timeLeft / 1000.0D)) + ChatColor.RED + " avant de pouvoir réexecuter cette action.");
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Une action est déjà en cours...");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(b.getDisplayName())) {
          if (Main.WG.getRegionManager(p.getWorld()).getApplicableRegions(h.getBlockClicked().getLocation()).size() > 0) {
            Set<ProtectedRegion> rgs = Main.WG.getRegionManager(p.getWorld()).getApplicableRegions(h.getBlockClicked().getLocation()).getRegions();
            for (ProtectedRegion rg : rgs) {
              if (rg.getOwners().contains(p.getUniqueId()) || rg.getMembers().contains(p.getUniqueId()))
                return; 
            } 
            if (h.getTask() == null) {
              Timestamp now = new Timestamp(System.currentTimeMillis());
              if (now.getTime() - h.getTimestamp().getTime() >= 120000L) {
                final Chest chest = (Chest)h.getBlockClicked().getState();
                if (data.selectedJob.getTask() == null) {
                  h.setTask(new TaskCreator(new CustomRunnable() {
                          private int time = 0;
                          
                          private RandomItem item = new RandomItem();
                          
                          public void customRun() {
                            if (chest.getLocation().distance(p.getLocation()) > 2.0D) {
                              p.sendMessage(ChatColor.RED + "Action annulée ! Rapprochez-vous de la cible !");
                              h.setTask(null);
                              cancel();
                            } 
                            if (this.time == 0)
                              p.sendMessage(ChatColor.GREEN + "Fouille en cours..."); 
                            if (this.time == 8)
                              p.sendMessage(ChatColor.GREEN + "Recherche d'objet rare en cours... (Ne bougez pas)"); 
                            if (this.time == 18) {
                              p.sendMessage(ChatColor.DARK_GREEN + "Objet trouvé !");
                              p.getInventory().addItem(h.getRandomize().getItem());
                              h.setTimestamp();
                              h.setTask(null);
                              cancel();
                            } 
                            this.time++;
                          }
                        },  false, 0L, 20L));
                } else {
                  p.sendMessage(ChatColor.RED + "Vous êtes déjà en train d'exécuter une action !");
                } 
              } else {
                long timeLeft = 120000L - (now.getTime() - h.getTimestamp().getTime());
                p.sendMessage(ChatColor.RED + "Vous devez attendre " + ChatColor.DARK_RED + TimerUtils.formatString((int)(timeLeft / 1000.0)) + ChatColor.RED + " avant de pouvoir réexecuter cette action.");
              } 
            } else {
              p.sendMessage(ChatColor.RED + "Une action est déjà en cours...");
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez être dans une région pour effectuer cette action");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(c.getDisplayName())) {
          if (Main.WG.getRegionManager(p.getWorld()).getApplicableRegions(h.getBlockClicked().getLocation()).size() > 0) {
            Set<ProtectedRegion> rgs = Main.WG.getRegionManager(p.getWorld()).getApplicableRegions(h.getBlockClicked().getLocation()).getRegions();
            for (ProtectedRegion rg : rgs) {
              if (rg.getOwners().contains(p.getUniqueId()) || rg.getMembers().contains(p.getUniqueId())) {
                MessageUtils.sendMessage((CommandSender)p, "§cVous ne pouvez pas cambrioler votre propre maison!");
                return;
              } 
            } 
            if (h.getTask() == null) {
              Timestamp now = new Timestamp(System.currentTimeMillis());
              if (now.getTime() - h.getTimestamp().getTime() >= 120000L) {
                final int proba = h.DoorOpeningProbability(h.getBlockClicked().getTypeId());
                if (data.selectedJob.getTask() == null) {
                  h.setTask(new TaskCreator(new CustomRunnable() {
                          private int time = 0;
                          
                          private boolean isOpen = false;
                          
                          public void customRun() {
                            if (h.getBlockClicked().getLocation().distance(p.getLocation()) > 2.0D) {
                              p.sendMessage(ChatColor.RED + "Action annulée ! Rapprochez-vous de la cible !");
                              h.setTask(null);
                              cancel();
                            } 
                            if (this.time == 0)
                              p.sendMessage(ChatColor.YELLOW + "Effraction du cylindre en cours..."); 
                            if (this.time == 8) {
                              p.sendMessage(ChatColor.YELLOW + "Tentative d'ouverture de la porte...");
                              double random = Math.random();
                              if (random <= proba / 100.0D)
                                this.isOpen = true; 
                            } 
                            if (this.time == 18) {
                              if (this.isOpen) {
                                h.setLastLocation(p.getLocation());
                                Location targetblock = h.getBlockClicked().getLocation();
                                p.teleport(targetblock.add(direction.getX(), 0.0D, direction.getZ()));
                                p.teleport(targetblock.add(Math.round(direction.getX()), 0.0D, Math.round(direction.getZ())));
                                p.sendMessage(ChatColor.GOLD + "La porte s'est ouverte !");
                                if (!WantedList.isWanted(p.getName())) {
                                  WantedList.addStars(p.getName(), 2);
                                  WantedList.wantedMessagePlace(p.getName(), 2, "pour cambriolage");
                                } 
                              } else {
                                p.sendMessage(ChatColor.GOLD + "Echec de l'ouverture !");
                              } 
                              h.setTask(null);
                              cancel();
                            } 
                            this.time++;
                          }
                        }, false, 0L, 20L));
                } else {
                  p.sendMessage(ChatColor.RED + "Vous êtes déjà en train d'exécuter une action !");
                } 
              } else {
                long timeLeft = 120000L - (now.getTime() - h.getTimestamp().getTime());
                p.sendMessage(ChatColor.RED + "Vous devez attendre " + ChatColor.DARK_RED + TimerUtils.formatString((int)(timeLeft / 1000.0D)) + ChatColor.RED + " avant de pouvoir réexecuter cette action.");
              } 
            } else {
              p.sendMessage(ChatColor.RED + "Une action est déjà en cours...");
            } 
          } else {
            p.sendMessage(ChatColor.RED + "Vous devez être dans une région pour effectuer cette action");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(d.getDisplayName())) {
          if (h.getLastLocation() != null) {
            if (h.getLastLocation().distance(p.getLocation()) <= 5.0D) {
              p.teleport(h.getLastLocation());
              h.setLastLocation(null);
            } else {
              p.sendMessage(ChatColor.DARK_RED + "Impossible de faire demi-tour. C'est pas la bonne porte");
            } 
          } else {
            p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas faire demi-tour !");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(f.getDisplayName())) {
          Timestamp now = new Timestamp(System.currentTimeMillis());
          if (now.getTime() - h.getTimestamp().getTime() >= 120000L) {
            h.setTimestamp();
            Caisse braquer = new Caisse(p);
            braquer.start();
            MessageUtils.sendMessage((CommandSender)p, "§aDémarrage du braquage");
            WantedList.addStars(p.getName(), 3);
            WantedList.wantedMessagePlace(p.getName(), 3, "pour Braquage d'une caisse enregistreuse");
          } else {
            long timeLeft = 120000L - (now.getTime() - h.getTimestamp().getTime());
            p.sendMessage(ChatColor.RED + "Vous devez attendre " + ChatColor.DARK_RED + TimerUtils.formatString((int)(timeLeft / 1000.0D)) + ChatColor.RED + " avant de pouvoir réexecuter cette action.");
          } 
        } 
      } 
      e.setCancelled(true);
    } 
  }
}
