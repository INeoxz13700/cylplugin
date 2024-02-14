package fr.karmaowner.election;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.utils.ItemUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Vote {
  public static ArrayList<String> candidats = new ArrayList<>();
  
  public static HashMap<String, String> electeurs = new HashMap<>();
  
  public static Inventory inv = Main.INSTANCE.getServer().createInventory(null, 9, "§cElection Municipale");
  
  public static Timestamp timer;
  
  public static boolean isStarted = false;
  
  private static TaskCreator task;
  
  public static void startElection(List<String> c) {
    candidats.addAll(c);
    isStarted = true;
    fillInventory();
    timer = new Timestamp(System.currentTimeMillis());
    Bukkit.broadcastMessage("§a[§d§lElection Municipale§a] §eUn vote a été lancé pour élire le prochain Maire ! §3/votemaire open pour voter !");
    task = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - Vote.timer.getTime() >= 3600000L) {
              String elu = Vote.getElu();
              if (elu != null) {
                Bukkit.broadcastMessage("§a[§d§lElection Municipale§a] Fin des élections. Le candidat gagnant est " + ChatColor.DARK_GREEN + elu);
                PlayerData data = PlayerData.getPlayerData(elu);
                if (data != null) {
                  JobsEvents.changePlayerJob(data, Jobs.Job.MAIRE.getName(), elu);
                  if (Bukkit.getPlayerExact(elu) != null)
                    Bukkit.getPlayerExact(elu).sendMessage("§a[§d§lElection Municipale§a] Félicitation, vous êtes le nouveau Maire de Jamala !"); 
                  cancel();
                } 
              } 
            } else if (now.getTime() - Vote.timer.getTime() >= 600000L) {
              Bukkit.broadcastMessage("§a[§d§lElection Municipale§a] §eUn vote a été lancé pour élire le prochain Maire ! §3/votemaire open pour voter !");
            } 
          }
        }, false, 0L, 20L);
  }
  
  public static void fillInventory() {
    for (String c : candidats) {
      inv.addItem(ItemUtils.getItem(397, (byte)3, 1, "§2" + c, Arrays.asList(new String[] { "§aClic-droit pour voter" })));
    } 
  }
  
  public static void voter(String playername, String candidat) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null)
      if (electeurs.get(playername) == null) {
        electeurs.put(playername, candidat);
        p.sendMessage(ChatColor.GREEN + "Votre vote a été comptabilisé.");
      } else {
        p.sendMessage(ChatColor.RED + "Vous avez déjà voté à un candidat.");
      }  
  }
  
  public static String getCandidatByItemName(String itemname) {
    for (String c : candidats) {
      if (itemname.contains(c))
        return c; 
    } 
    return null;
  }
  
  public static void listCandidats(String playername) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      int i = 0;
      p.sendMessage("§4---------------------------");
      for (String c : candidats) {
        p.sendMessage("§6 " + i + "- §e" + c);
        i++;
      } 
      p.sendMessage("§4---------------------------");
    } 
  }
  
  public static void cancelElection(String playername) {
    if (isStarted) {
      if (task != null)
        task.cancelTask(); 
      candidats = new ArrayList<>();
      electeurs = new HashMap<>();
      isStarted = false;
      timer = null;
      inv.clear();
      Bukkit.broadcastMessage("§a[§d§lElection Municipale§a] §4Les élections ont été annulé par un administrateur.");
      task = null;
    } else if (Bukkit.getPlayerExact(playername) != null) {
      Bukkit.getPlayerExact(playername).sendMessage(ChatColor.RED + "Aucune élection en cours.");
    } 
  }
  
  public static void stopElection() {
    timer = new Timestamp(0L);
  }
  
  public static String getElu() {
    int max = 0, score = 0;
    String elu = null;
    for (String c : candidats) {
      score = EluScore(c);
      if (score > max) {
        max = score;
        elu = c;
      } 
    } 
    return elu;
  }
  
  public static int EluScore(String candidat) {
    int voix = 0;
    for (String c : electeurs.values()) {
      if (c.equalsIgnoreCase(candidat))
        voix++; 
    } 
    return voix;
  }
  
  public static TaskCreator getTask() {
    return task;
  }
}
