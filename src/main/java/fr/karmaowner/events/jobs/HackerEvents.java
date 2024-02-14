package fr.karmaowner.events.jobs;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.hacker.MakeHackingGame;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.TimerUtils;
import fr.karmaowner.wantedlist.WantedList;
import java.sql.Timestamp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HackerEvents implements Listener {
  private MakeHackingGame mhg = null;

  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (inventory.getName().equals(Jobs.NAMEACTIONINVENTORY)) {
      if (item != null && 
        item.getType() != Material.AIR && 
        data.selectedJob instanceof Hacker) {
        final Hacker h = (Hacker)data.selectedJob;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Hacker.Action a = Hacker.Action.HACKERATM;
        Hacker.Action b = Hacker.Action.HACKERBANQUE;
        Hacker.Action c = Hacker.Action.HACKERBDDCOMICO;
        if (item.getItemMeta() != null && 
          item.getItemMeta().getDisplayName() != null)
          if (item.getItemMeta().getDisplayName().equals(a.getDisplayName())) {
            if (now.getTime() - h.timer.getTime() >= 300000L) {
              if (Jobs.Job.GENDARME.onlinePlayers().size() < 2) {
                p.sendMessage(ChatColor.RED + "Il doit y avoir au moins 2 gendarmes de connecté pour effectuer cette action !");
                return;
              }
              h.timer = new Timestamp(System.currentTimeMillis());
              h.startHacking(a, p);
              if (!WantedList.isWanted(p.getName())) {
                WantedList.addStars(p.getName(), 2);
                WantedList.wantedMessagePlace(p.getName(), 2, "pour hacking d'atm");
              } 
              int time = 60;
              h.setTask(new TaskCreator(new CustomRunnable() {
                      public void customRun() {
                        if (h.getGame() == null)
                          return; 
                        h.getGame().end();
                        p.sendMessage(ChatColor.DARK_RED + "Echec de la mission:" + ChatColor.RED + " vous n'avez pas accompli à temps la mission !");
                      }
                    },  false, 20L * time));
            } else {
              int seconds = (int)((300000L - (now.getTime() - h.timer.getTime())) * 1.0D / 1000.0D);
              p.sendMessage(ChatColor.RED + "Vous devez attendre encore " + ChatColor.DARK_RED + TimerUtils.formatString(seconds) + ChatColor.RED + " avant d'effectuer cette action !");
            }
          } else if (item.getItemMeta().getDisplayName().equals(b.getDisplayName())) {
            if (now.getTime() - h.timer.getTime() >= 1200000L) {
              h.startHacking(b, p);
              if (!WantedList.isWanted(p.getName())) {
                WantedList.addStars(p.getName(), 2);
                WantedList.wantedMessagePlace(p.getName(), 2, "pour piratage de la porte d'entrée de la banque");
              } 
              int time = 60;
              h.setTask(new TaskCreator(new CustomRunnable() {
                      public void customRun() {
                        if (h.getGame() == null)
                          return; 
                        h.getGame().end();
                        h.timer = new Timestamp(System.currentTimeMillis());
                        p.sendMessage(ChatColor.DARK_RED + "Echec de la mission:" + ChatColor.RED + " vous n'avez pas accompli à temps la mission !");
                      }
                    },  false, 20L * time));
            } else {
              int seconds = (int)((1200000L - (now.getTime() - h.timer.getTime())) * 1.0D / 1000.0D);
              p.sendMessage(ChatColor.RED + "Vous devez attendre encore " + ChatColor.DARK_RED + TimerUtils.formatString(seconds) + ChatColor.RED + " avant d'effectuer cette action !");
            } 
          } else if (item.getItemMeta().getDisplayName().equals(c.getDisplayName())) {
            if (now.getTime() - h.timer.getTime() >= 1800000L) {
              h.timer = new Timestamp(System.currentTimeMillis());
              h.startHacking(c, p);
              if (!WantedList.isWanted(p.getName())) {
                WantedList.addStars(p.getName(), 2);
                WantedList.wantedMessagePlace(p.getName(), 2, "pour piratage de la base de donnée du comico");
              } 
              int time = 45;
              h.setTask(new TaskCreator(new CustomRunnable() {
                      public void customRun() {
                        if (h.getGame() == null)
                          return; 
                        h.getGame().end();
                        p.sendMessage(ChatColor.DARK_RED + "Echec de la mission:" + ChatColor.RED + " vous n'avez pas accompli à temps la mission !");
                      }
                    },  false, 20L * time));
            } else {
              int seconds = (int)((1200000L - (now.getTime() - h.timer.getTime())) * 1.0D / 1000.0D);
              p.sendMessage(ChatColor.RED + "Vous devez attendre encore " + ChatColor.DARK_RED + TimerUtils.formatString(seconds) + ChatColor.RED + " avant d'effectuer cette action !");
            } 
          }  
      } 
      e.setCancelled(true);
    } else if (inventory.getName().equals("§2Supprimer un avis de recherche") && 
      item != null && 
      item.getType() != Material.AIR) {
      String plname = WantedList.getWantedPlayerNameBySlot(e.getSlot());
      if (plname != null) {
        Main.Log(plname);
        WantedList.stopWanted(plname);
        MessageUtils.sendMessage((CommandSender)p, "§aVous venez de supprimer l'avis de recherche d'un individu");
        Player wantedPl = Bukkit.getPlayerExact(plname);
        if (wantedPl != null)
          MessageUtils.sendMessage((CommandSender)wantedPl, "§aUn hackeur a supprimé votre avis de recherche"); 
        p.closeInventory();
      } 
    } 
  }
  
  @EventHandler
  public void onPlayerDisconnect(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    PlayerData pData = PlayerData.getPlayerData(p.getName());
    if (pData.selectedJob instanceof Hacker) {
      Hacker h = (Hacker)pData.selectedJob;
      if (h.getTask() != null) {
        h.getTask().cancelTask();
        h.setTask(null);
      } 
    } 
  }

}
