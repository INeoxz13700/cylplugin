package fr.karmaowner.jobs.missions.type;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.missions.Missions;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class KillTask extends GeneralType {
  private int count;
  
  private EntityType entity;
  
  private Jobs.Job[] job;
  
  private int tempCount;
  
  public KillTask(int count, EntityType entity, String desc) {
    super(desc);
    this.count = count;
    this.entity = entity;
  }
  
  public KillTask(int count, EntityType entity, String desc, long duration) {
    super(desc, duration);
    this.count = count;
    this.entity = entity;
  }
  
  public KillTask(int count, String desc, Jobs.Job[] job) {
    this(count, EntityType.PLAYER, desc);
    this.job = job;
  }
  
  public KillTask(int count, String desc, Jobs.Job[] job, long duration) {
    this(count, EntityType.PLAYER, desc, duration);
    this.job = job;
  }
  
  public Jobs.Job[] getJob() {
    return this.job;
  }
  
  public boolean isJob(Jobs.Job j) {
    for (Jobs.Job j2 : this.job) {
      if (j2.getName().equals(j.getName()))
        return true; 
    } 
    return false;
  }
  
  public void startMission() {
    super.startMission();
    this.tempCount = this.count;
  }
  
  public void startTask(final Player p) {
    PlayerData data = PlayerData.getPlayerData(p.getName());
    Missions missions = (Missions)data.selectedJob;
    final KillTask kt = (KillTask)missions.getInProgress().getType();
    startMission();
    informations(p.getName());
    updateDurationMessage(p);
    setTask(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              if (Bukkit.getPlayerExact(p.getName()) == null) {
                KillTask.this.failed(p);
                cancel();
                return;
              }
              if (kt.getTempCount() <= 0)
                kt.setFinished(true); 
              if (!KillTask.this.isFinished()) {
                if (KillTask.this.getDuration() > 0L) {
                  Timestamp now = new Timestamp(System.currentTimeMillis());
                  if (now.getTime() - KillTask.this.getStart().getTime() >= KillTask.this.getDuration()) {
                    KillTask.this.failed(p);
                    cancel();
                  } 
                } 
              } else {
                KillTask.this.accomplished(p);
                cancel();
              } 
            }
          },  false, 0L, 20L));
  }
  
  public int getTempCount() {
    return this.tempCount;
  }
  
  public void setTempCount(int temp) {
    this.tempCount = temp;
  }
  
  public void informations(String playername) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      p.sendMessage(ChatColor.GOLD + ChatColor.UNDERLINE.toString() + getDesc());
      p.sendMessage(ChatColor.WHITE + "--------------");
      if (this.entity == EntityType.PLAYER) {
        p.sendMessage(ChatColor.RED + "élimination:");
        for (Jobs.Job j : this.job)
          p.sendMessage(j.getPrefix()); 
      } else {
        p.sendMessage(ChatColor.RED + "élimination:");
        p.sendMessage(ChatColor.DARK_RED + this.entity.getName());
      } 
      p.sendMessage(ChatColor.YELLOW + "--------------");
      p.sendMessage(ChatColor.GREEN + "restant: " + ChatColor.DARK_GREEN.toString() + this.tempCount);
      p.sendMessage(ChatColor.LIGHT_PURPLE + "pour obtenir des informations /jobs missions about  ou /jbs ma");
    } 
  }
  
  public int getCount() {
    return this.count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  public EntityType getEntity() {
    return this.entity;
  }
  
  public void setEntity(EntityType entity) {
    this.entity = entity;
  }
  
  public static KillTask loadData(String key) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    long duration = f.getLong(key + ".GeneralType.duration");
    String desc = f.getString(key + ".GeneralType.desc");
    int count = f.getInt(key + ".KillTask.count");
    String entity = f.getString(key + ".KillTask.entity");
    List<String> jobs = f.getStringList(key + ".KillTask.jobs");
    if (entity == null) {
      if (duration == 0L)
        return new KillTask(count, desc, Jobs.Job.getJobs(jobs)); 
      return new KillTask(count, desc, Jobs.Job.getJobs(jobs), duration);
    } 
    if (duration == 0L)
      return new KillTask(count, EntityType.fromName(entity), desc); 
    return new KillTask(count, EntityType.fromName(entity), desc, duration);
  }
  
  public static void saveData(String key, HashMap<String, Object> infos) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    Object entity = infos.get("entity");
    Object jobs = infos.get("jobs");
    if (jobs != null) {
      f.set(key + ".KillTask.jobs", jobs);
    } else if (entity != null) {
      f.set(key + ".KillTask.entity", entity);
    } 
    int count = (Integer) infos.get("count");
    f.set(key + ".KillTask.count", count);
  }
  
  public void saveData(String key) {
    super.saveData(key);
    FileConfiguration f = Main.INSTANCE.getConfig();
    f.set(key + ".GeneralType.task", "KillTask");
    List<String> listJobs = new ArrayList<>();
    for (Jobs.Job j : this.job)
      listJobs.add(j.getName()); 
    if (listJobs.size() > 0) {
      f.set(key + ".KillTask.jobs", listJobs);
    } else if (this.entity != null) {
      f.set(key + ".KillTask.entity", this.entity);
    } 
    f.set(key + ".KillTask.count", this.count);
  }
}
