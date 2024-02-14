package fr.karmaowner.wantedlist;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.CustomConcurrentHashMap;
import fr.karmaowner.utils.FileUtils;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.TimerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WantedList {
  private static CustomConcurrentHashMap<String, Integer> list = new CustomConcurrentHashMap();
  
  private static final int TimerSecondsSemer = 180;
  
  private static CustomConcurrentHashMap<String, TaskCreator> taskList = new CustomConcurrentHashMap();
  
  public static String inventoryName = "§5Avis de recherche";
  
  private static FileUtils f = new FileUtils("WantedList", "");
  
  public static Inventory getList() {
    Inventory inv = Main.INSTANCE.getServer().createInventory(null, 27, inventoryName);
    int i = 1;
    for (Map.Entry<String, Integer> wanted : (Iterable<Map.Entry<String, Integer>>)list.entrySet()) {
      if (i >= 27)
        break; 
      Player p = Bukkit.getPlayerExact(wanted.getKey());
      if (p != null) {
        PlayerData data = PlayerData.getPlayerData(wanted.getKey());
        if (data != null && !(data.selectedJob instanceof fr.karmaowner.jobs.Security) && !(data.selectedJob instanceof fr.karmaowner.jobs.Pompier) && !(data.selectedJob instanceof fr.karmaowner.jobs.Medecin)) {
          String coord = "§4x=" + p.getLocation().getBlockX() + " y=" + p.getLocation().getBlockY() + " z=" + p.getLocation().getBlockZ();
          inv.addItem(ItemUtils.getItem(397, (byte)3, 1, "§b§l" +
                  String.join(" ", (CharSequence[])data.getIdentity()), Arrays.asList("§eétoiles: " + wanted.getValue(), "§ccoordonnées: " + coord)));
          i++;
        } 
      } 
    } 
    return inv;
  }
  
  public static ArrayList<String> getPlayers() {
    ArrayList<String> players = new ArrayList<>();
    for (String pl : list.keySet()) {
      Player p = Bukkit.getPlayerExact(pl);
      if (p != null)
        players.add(pl); 
    } 
    return players;
  }
  
  public static void openInventory(Player p) {
    p.openInventory(getList());
  }
  
  public static boolean isWanted(String playername) {
    return list.containsKey(playername);
  }
  
  public static String getWantedPlayerNameBySlot(int slot) {
    int i = 0;
    for (String pl : list.keySet()) {
      Player p = Bukkit.getPlayerExact(pl);
      if (p != null) {
        if (i == slot)
          return pl; 
        i++;
      } 
    } 
    return null;
  }
  
  private static void addPlayer(String playername, int stars) {
    if (!isWanted(playername)) {
      TaskSemer(playername);
      list.put(playername, stars);
    } 
  }
  
  public static void TaskSemer(final String playername) {
    TaskCreator checkTask = (TaskCreator)taskList.get(playername);
    Player me = Bukkit.getPlayerExact(playername);
    if (checkTask == null && me != null)
      taskList.put(playername, new TaskCreator(new CustomRunnable() {
              private int elapsedTime = 0;
              
              public void customRun() {
                Player me = Bukkit.getPlayerExact(playername);
                if (me != null) {
                  PlayerUtils player = new PlayerUtils();
                  player.setPlayer(me);
                  ArrayList<Player> playersAround = player.getNearbyPlayers(30.0D);
                  for (Player p : playersAround) {
                    PlayerData data = PlayerData.getPlayerData(p.getName());
                    if (data != null && data.selectedJob instanceof fr.karmaowner.jobs.Security) {
                      PlayerUtils.sendMessagePlayer(playername, "§4Les forces de l'ordre sont à votre poursuite");
                      if (this.elapsedTime != 0)
                        this.elapsedTime = 0; 
                      return;
                    } 
                  } 
                  int remainingTime = 180 - this.elapsedTime;
                  if (remainingTime <= 0) {
                    WantedList.stopWanted(playername);
                    PlayerUtils.sendMessagePlayer(playername, "§aVous avez semé les flics ! Vous n'êtes plus recherché");
                  } else {
                    PlayerUtils.sendMessagePlayer(playername, "§4Il vous reste §c" + TimerUtils.formatString(remainingTime) + "§4 avant de semer les forces de l'ordre");
                    this.elapsedTime += 10;
                  } 
                } else {
                  cancel();
                  WantedList.taskList.remove(playername);
                } 
              }
            },false, 20L, 200L)); 
  }
  
  public static void wantedMessagePlace(String playername, int stars, String place) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      Bukkit.broadcastMessage("§a[§dAvis de recherche§a] §b" + String.join(" ", (CharSequence[])data.getIdentity()) + " §3est recherché " + place + " avec §e" + stars + " étoiles");
    } 
  }
  
  public static void wantedMessage(String playername, int stars) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      Bukkit.broadcastMessage("§a[§dAvis de recherche§a] §b" + String.join(" ", (CharSequence[])data.getIdentity()) + " §3est recherché avec §e" + stars + " étoiles");
    } 
  }
  
  public static void addStars(String playername, int stars) {
    if (stars <= 5) {
      Player p = Bukkit.getPlayerExact(playername);
      if (p != null) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " defender set 0");
      }
      addPlayer(playername, stars);
    } 
  }
  
  public static void stopWanted(String playername) {
    if (isWanted(playername)) {
      Player p = Bukkit.getPlayerExact(playername);
      if (p != null)
      {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + playername + " defender set 1000");
      }
      removePlayer(playername);
    } 
  }
  
  public static int WantedPrice(int stars) {
    switch (stars) {
      case 1:
        return 250;
      case 2:
        return 500;
      case 3:
        return 1000;
      case 4:
        return 2500;
      case 5:
        return 5000;
    } 
    return -1;
  }
  
  private static void removePlayer(String playername) {
    list.remove(playername);
    TaskCreator task = (TaskCreator)taskList.get(playername);
    if (task != null) {
      task.cancelTask();
      taskList.remove(playername);
    } 
  }
  
  public static int getStars(String playername) {
    return (Integer) list.get(playername);
  }
  
  public static void loadData() {
    /*if (!f.directoryExist())
      f.createFile(); 
    f.loadFileConfiguration();
    if (f.getFileConfiguration() != null) {
      FileConfiguration config = f.getFileConfiguration();
      String tag = "wanted";
      if (config.getConfigurationSection(tag) != null) {
        for (String key : config.getConfigurationSection(tag).getKeys(false)) {
          int stars = config.getInt(tag + "." + key);
          addPlayer(key, stars);
        } 
        config.set(tag, null);
      } 
    }*/
  }
  
  public static void saveData() {
    /*Main.Log("Wanted List Data saving...");
    FileConfiguration config = f.getFileConfiguration();
    String tag = "wanted";
    for (Map.Entry<String, Integer> wanted : (Iterable<Map.Entry<String, Integer>>)list.entrySet())
      config.set(tag + "." + (String)wanted.getKey(), wanted.getValue()); 
    f.saveConfig();
    Main.Log("Wanted List Data saved");*/
  }
}
