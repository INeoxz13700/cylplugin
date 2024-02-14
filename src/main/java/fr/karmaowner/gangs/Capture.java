package fr.karmaowner.gangs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Militaire;
import fr.karmaowner.utils.Delay;
import fr.karmaowner.utils.FileUtils;
import fr.karmaowner.utils.RegionUtils;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Capture {
  private ProtectedRegion rg;
  
  private CaptureState cs;
  
  private String gangOwnerName;
  
  private String gangStartedCapture;
  
  private Timestamp duration;
  
  private Timestamp timerStart;

  private long lastTimeMessageDisplayed;
  
  private World world;
  
  private double percent;
  
  private Rewards rewards;
  
  private TaskCreator task;
  
  private HashMap<String, Long> delays = new HashMap<>();
  
  public static FileUtils file = new FileUtils("Captures", "");
  
  public static HashMap<String, Capture> captures = new HashMap<>();
  
  public Capture(Player p, String rgName) {
    ProtectedRegion r = RegionUtils.getRegionByName(rgName, p.getWorld().getName());
    if (r != null && !isCaptureExist(rgName)) {
      this.rg = r;
      this.cs = CaptureState.WILDERNESS;
      this.gangOwnerName = null;
      this.duration = new Timestamp(0L);
      this.world = p.getWorld();
      this.percent = 0.0D;
      this.rewards = new Rewards(this, 3600, null);
      this.rewards.defineRewards(new ChestMoneys());
      this.rewards.defineRewards(new ChestDrugs());
      this.rewards.defineRewards(new ChestVehicles());
      this.rewards.defineRewards(new ChestWeapon());
      this.rewards.startTask(0.5D);
      this.task = new TaskCreator(runTask(), false, 0L, 20L);
      p.sendMessage(ChatColor.GREEN + "Région de capture créée avec succès !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette région n'existe pas !");
    } 
  }
  
  public Capture(ProtectedRegion rg, CaptureState cs, String gangName, Timestamp duration, World world, int percent, Timestamp timer) {
    this.rg = rg;
    this.cs = cs;
    this.percent = percent;
    this.gangOwnerName = gangName;
    this.world = world;
    this.duration = duration;
    this.rewards = new Rewards(this, 3600, timer);
    this.rewards.defineRewards(new ChestMoneys());
    this.rewards.defineRewards(new ChestDrugs());
    this.rewards.defineRewards(new ChestVehicles());
    this.rewards.defineRewards(new ChestWeapon());
    this.rewards.startTask(0.5D);
    this.task = new TaskCreator(runTask(), false, 0L, 20L);
  }
  
  private CustomRunnable runTask() {
    return new CustomRunnable() {
        public void customRun() {
          List<Player> playersInRegion = RegionUtils.getPlayersInRegion(Capture.this.rg);
          List<Player> toRemove = new ArrayList<>();
          for (Player p : playersInRegion) {
            if (p.hasPermission("cylrp.gang.bypassregion")) {
              toRemove.add(p);
              continue;
            } 
            PlayerData pData = PlayerData.getPlayerData(p.getName());
            if (!(pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste) && !(pData.selectedJob instanceof fr.karmaowner.jobs.Militaire))
            {
              toRemove.add(p);
            }
            else if(pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste && !pData.hasGang())
            {
              toRemove.add(p);
            }
          }
          playersInRegion.removeAll(toRemove);
          Capture.this.startCapture(playersInRegion);
          Capture.this.updateCapture(playersInRegion);
          Capture.this.dropDelays(playersInRegion);
        }
      };
  }
  
  private void dropDelays(List<Player> playersInRegion) {
    if (!Delay.isDelay("dropCaptureUselessDelays")) {
      Delay.newInstance(60.0D, "dropCaptureUselessDelays");
      ArrayList<String> toRemove = new ArrayList<>();
      for (String plname : this.delays.keySet()) {
        String notContained = "";
        for (Player p : playersInRegion) {
          if (plname.contains(p.getName()))
            notContained = plname; 
        } 
        if (!notContained.isEmpty())
          toRemove.add(notContained); 
      } 
      for (String rm : toRemove)
        this.delays.remove(rm); 
    } 
  }
  
  private void startCapture(List<Player> playersInRegion) {
    if (this.cs == CaptureState.WILDERNESS || this.cs == CaptureState.CATCHED) {
      Timestamp now = new Timestamp(System.currentTimeMillis());
      if (this.cs == CaptureState.CATCHED && now.getTime() - this.duration.getTime() < 60000L)
      {
        List<Player> playersInRegionNotMemberOfRegion = new ArrayList<>();
        if(gangOwnerName != null)
        {
          for (Player p : playersInRegion)
          {
            PlayerData data = PlayerData.getPlayerData(p.getName());
            if (!data.hasGang() || (data.hasGang() && !data.gangName.equals(gangOwnerName)))
              playersInRegionNotMemberOfRegion.add(p);
          }
        }

        sendMessage(playersInRegionNotMemberOfRegion, ChatColor.RED + "Capture pour le moment impossible car la zone a été récemment capturée !", "captured_recently", 10000L);
      }
      else if (canCapture(playersInRegion) && !hasOnlyArmyInRegion(playersInRegion))
      {
        List<String> gangs = getGangsInRegion(playersInRegion);
        if (!gangs.contains(this.gangOwnerName)) {
          this.cs = CaptureState.CATCHING;
          this.gangStartedCapture = gangs.get(0);
          if((System.currentTimeMillis() - lastTimeMessageDisplayed) / 1000f >= 60*5)
          {
            lastTimeMessageDisplayed = System.currentTimeMillis();
            Bukkit.broadcastMessage(ChatColor.YELLOW + "La région " + ChatColor.RED + this.rg.getId() + ChatColor.YELLOW + " est en train de se faire capturer !");
          }
        }
      }
      else if (hasOnlyArmyInRegion(playersInRegion))
      {
        if (this.gangOwnerName != null) {
          this.cs = CaptureState.CATCHING;
          this.gangStartedCapture = null;
          if((System.currentTimeMillis() - lastTimeMessageDisplayed) / 1000f >= 60*5) {
            lastTimeMessageDisplayed = System.currentTimeMillis();
            Bukkit.broadcastMessage("§aLes militaires sont en intervention dans la zone de capture §2" + this.rg.getId());
          }
        } 
      }
      else
      {
        sendMessage(playersInRegion, ChatColor.RED + "Capture désactivée en raison du nombre de gangs ou de militaires présent dans la région !", "capture_disabled", 10000L);
      } 
    } 
  }
  
  private boolean hasOnlyArmyInRegion(List<Player> playersInRegion) {
    if (playersInRegion.isEmpty()) return false;
    for (Player p : playersInRegion)
    {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      if (!(data.selectedJob instanceof fr.karmaowner.jobs.Militaire))
        return false; 
    } 
    return true;
  }
  
  private void updateCapture(List<Player> playersInRegion) {
    isCatching(playersInRegion);
    regionCatched(playersInRegion);
  }
  
  private void isCatching(List<Player> playersInRegion) {
    if (this.cs == CaptureState.CATCHING) {
      List<String> gangs = getGangsInRegion(playersInRegion);
      boolean hasOnlyArmyInRegion = hasOnlyArmyInRegion(playersInRegion);
      if (this.percent < 100.0D) {
        if (canCapture(playersInRegion) && !hasOnlyArmyInRegion) {

          List<Player> playersGangInRegion = getPlayersInRegionByGang(playersInRegion, this.gangStartedCapture);

          if (gangStartedCapture == null || !gangs.contains(this.gangStartedCapture)) {
            this.gangStartedCapture = gangs.get(0);
            this.percent = 0.0D;
          }

          this.percent += 0.1D * playersGangInRegion.size();
          sendMessageToPlayerInRegionByGang(playersInRegion, this.gangStartedCapture, ChatColor.GREEN + "Capture à " + ChatColor.DARK_GREEN + Math.min(100,getPercent()) + "%", "capturing", 1000L);
        } else if (hasOnlyArmyInRegion) {
          if (this.gangOwnerName != null)
          {
            if(gangStartedCapture != null)
            {
              percent = 0.0D;
              gangStartedCapture = null;
            }
            this.percent += 0.1D * playersInRegion.size();
            sendMessage(playersInRegion, "§bReconquête à §3" + ChatColor.DARK_GREEN + Math.min(100,getPercent()) + "%", "capturing", 1000L);
          } 
        } else {
          if(playersInRegion.isEmpty())
          {
            if(gangOwnerName != null)
            {
              setCaptureState(CaptureState.CATCHED);
            }
            else
            {
              setCaptureState(CaptureState.WILDERNESS);
            }
            percent = 0.0D;
            gangStartedCapture = null;
          }
          else
          {
            sendMessage(playersInRegion, ChatColor.RED + "Capture désactivée en raison du nombre de gangs ou de militaires présent dans la région !", "capture_disabled", 10000L);
          }
        }
      } else if (hasOnlyArmyInRegion) {
        this.cs = CaptureState.WILDERNESS;
        this.percent = 0.0D;
        this.gangStartedCapture = null;
        setCaptureOwner(null);
        this.lastTimeMessageDisplayed = 0;
        this.duration.setTime(0L);
        this.sendMessage(getMilitaryPlayersInRegion(playersInRegion), "§aLe térritoire est de nouveau sous contrôle!");
      } else if (gangs.size() == 1) {
        this.cs = CaptureState.CATCHED;
        this.percent = 0.0D;
        setCaptureOwner(this.gangStartedCapture);
        this.gangStartedCapture = null;
        this.lastTimeMessageDisplayed = 0;
        this.duration.setTime(0L);
        this.sendMessage(getPlayersInRegionByGang(playersInRegion,gangOwnerName), "§aLe térritoire vous appartient désormais!");
      } 
    } 
  }
  
  private boolean canCapture(List<Player> playersInRegion)
  {
    List<String> gangs = getGangsInRegion(playersInRegion);
    return (gangs.size() == 1);
  }
  
  private void regionCatched(List<Player> playersInRegion) {
    Timestamp now = new Timestamp(System.currentTimeMillis());
    if (now.getTime() - this.duration.getTime() >= 3600000L) {
      this.duration.setTime(System.currentTimeMillis());
      GangData data = getGangOwnerData();
      if (data != null) {
        data.setRankingPoints(data.getRankingPoints() + 1);
        data.sendMessageAll(ChatColor.GOLD + "Vous venez de remporter 1 point de classement de gang pour votre exploit de capture !");
      } 
    } 
  }
  
  public Rewards getRewards() {
    return this.rewards;
  }
  
  public World getWorld() {
    return this.world;
  }
  
  public Timestamp getTimer() {
    return this.timerStart;
  }
  
  public void setTimer(long time) {
    this.timerStart = new Timestamp(time);
  }
  
  public ProtectedRegion getRegion() {
    return this.rg;
  }
  
  public GangData getGangOwnerData() {
    if (this.gangOwnerName != null)
      return GangData.getGangData(this.gangOwnerName); 
    return null;
  }
  
  public String getCaptureOwner() {
    return this.gangOwnerName;
  }
  
  public void setCaptureOwner(String gangName) {
    this.gangOwnerName = gangName;
  }
  
  public void setDuration() {
    this.duration = new Timestamp(System.currentTimeMillis());
  }

  public void sendMessage(List<Player> players, String msg) {
    for (Player p : players) {
      p.sendMessage(msg);
    }
  }

  public void sendMessage(List<Player> players, String msg, String delayId, long delayMilisc) {
    for (Player p : players) {
      if (!this.delays.containsKey(p.getName() + "_" + delayId)) {
        this.delays.put(p.getName() + "_" + delayId, System.currentTimeMillis());
        p.sendMessage(msg);
        continue;
      } 
      if (System.currentTimeMillis() - (Long) this.delays.get(p.getName() + "_" + delayId) >= delayMilisc)
        this.delays.remove(p.getName() + "_" + delayId); 
    } 
  }
  
  public void sendMessageToPlayerInRegionByGang(List<Player> playersInRegion, String gangName, String msg, String delayId, long delayMilisc) {
    if (!this.delays.containsKey(gangName + "_" + delayId)) {
      this.delays.put(gangName + "_" + delayId, System.currentTimeMillis());
      for (Player p : getPlayersInRegionByGang(playersInRegion, gangName)) {
        p.sendMessage(msg);
      }
    }
    else
    {
      if (System.currentTimeMillis() - (Long) this.delays.get(gangName + "_" + delayId) >= delayMilisc)
        this.delays.remove(gangName + "_" + delayId);
    }
  }
  
  public List<Player> getPlayersInRegionByGang(List<Player> playersInRegion, String gangName) {
    List<Player> plys = new ArrayList<>();
    for (Player p : playersInRegion) {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      if (data.gangName != null && !data.gangName.isEmpty() && 
        data.gangName.equals(gangName))
        plys.add(p); 
    } 
    return plys;
  }


  public List<Player> getMilitaryPlayersInRegion(List<Player> playersInRegion) {
    List<Player> plys = new ArrayList<>();
    for (Player p : playersInRegion) {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      if (data.selectedJob instanceof Militaire) plys.add(p);
    }
    return plys;
  }


  public ArrayList<String> getGangsInRegion(List<Player> playersInRegion) {
    ArrayList<String> gangs = new ArrayList<>();
    for (Player p : playersInRegion) {
      PlayerData data = PlayerData.getPlayerData(p.getName());
      if (!gangs.contains(data.gangName))
        gangs.add(data.gangName); 
    } 
    return gangs;
  }
  
  public CaptureState getCaptureState() {
    return this.cs;
  }
  
  public Timestamp getDuration() {
    return this.duration;
  }
  
  public void setCaptureState(CaptureState cs) {
    this.cs = cs;
  }
  
  public static Capture getCapture(String rgName) {
    for (String rg : captures.keySet()) {
      if (rg.equalsIgnoreCase(rgName))
        return captures.get(rg); 
    } 
    return null;
  }
  
  public static void deleteCapture(Player p, String rgName) {
    if (isCaptureExist(rgName)) {
      captures.remove(rgName);
      p.sendMessage(ChatColor.GREEN + "Zone de capture supprimée !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette zone de capture n'existe pas !");
    } 
  }
  
  public static boolean isCaptureExist(String rgName) {
    for (String rg : captures.keySet()) {
      if (rg.equals(rgName))
        return true; 
    } 
    return false;
  }
  
  public static void loadData() {
    if (!file.directoryExist()) {
      file.createFile();
      file.loadFileConfiguration();
    } else if (file.getFileConfiguration() == null) {
      file.loadFileConfiguration();
    } 
    String section = "Regions";
    if (file.getFileConfiguration().getConfigurationSection(section) != null)
      for (String key : file.getFileConfiguration().getConfigurationSection(section).getKeys(false)) {
        String rgName = key;
        String world = file.getFileConfiguration().getString(section + "." + key + ".world");
        ProtectedRegion r = RegionUtils.getRegionByName(rgName, world);
        if (r != null) {
          CaptureState cs = CaptureState.getCaptureByName(file.getFileConfiguration().getString(section + "." + key + ".captureState"));
          String gangName = file.getFileConfiguration().getString(section + "." + key + ".gangName");
          Timestamp duration = new Timestamp(file.getFileConfiguration().getLong(section + "." + key + ".duration"));
          int percent = file.getFileConfiguration().getInt(section + "." + key + ".percent");
          Timestamp timer = new Timestamp(file.getFileConfiguration().getLong(section + "." + key + ".recompenseTimer"));
          Capture c = new Capture(r, cs, gangName, duration, Bukkit.getWorld(world), percent, timer);
          c.setTimer(duration.getTime());
          captures.put(rgName, c);
        } 
      }  
    file.getFileConfiguration().set(section, null);
  }
  
  public static void saveData() {
    Main.Log("Capture Data saving...");
    String section = "Regions";
    for (Capture c : captures.values()) {
      if (c.rg != null) {
        if (c.cs != null)
          file.getFileConfiguration().set(section + "." + c.rg.getId() + ".captureState", c.cs.toString()); 
        if (c.gangOwnerName != null && !c.gangOwnerName.isEmpty())
          file.getFileConfiguration().set(section + "." + c.rg.getId() + ".gangName", c.gangOwnerName); 
        if (c.duration != null)
          file.getFileConfiguration().set(section + "." + c.rg.getId() + ".duration", c.duration.getTime());
        if (c.getRewards().getTimer() != null)
          file.getFileConfiguration().set(section + "." + c.rg.getId() + ".recompenseTimer", c.getRewards().getTimer().getTime());
        if (c.world != null)
          file.getFileConfiguration().set(section + "." + c.rg.getId() + ".world", c.world.getName()); 
        file.getFileConfiguration().set(section + "." + c.rg.getId() + ".percent", c.percent);
      } 
    } 
    file.saveConfig();
    Main.Log("Capture Data saved");
  }
  
  public double getPercent() {
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.parseDouble(df.format(this.percent).replaceFirst(",", "."));
  }
  
  public void setPercent(double d) {
    this.percent = d;
  }
  
  public void resetCapture() {
    setCaptureState(CaptureState.WILDERNESS);
    setCaptureOwner(null);
  }
  
  public TaskCreator getTask() {
    return this.task;
  }
}
