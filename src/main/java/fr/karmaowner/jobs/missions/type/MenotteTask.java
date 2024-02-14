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
import org.bukkit.entity.Player;

public class MenotteTask extends GeneralType {
  private int count;
  
  private Jobs.Job[] job;
  
  private int tempCount;
  
  public MenotteTask(int count, String desc) {
    super(desc);
    this.count = count;
  }
  
  public MenotteTask(int count, String desc, long duration) {
    super(desc, duration);
    this.count = count;
  }
  
  public MenotteTask(int count, String desc, Jobs.Job[] job) {
    this(count, desc);
    this.job = job;
  }
  
  public MenotteTask(int count, String desc, Jobs.Job[] job, long duration) {
    this(count, desc, duration);
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
    final MenotteTask kt = (MenotteTask)missions.getInProgress().getType();
    startMission();
    informations(p.getName());
    updateDurationMessage(p);
    setTask(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              if (Bukkit.getPlayerExact(p.getName()) == null) {
                MenotteTask.this.failed(p);
                cancel();
                return;
              } 
              if (kt.getTempCount() <= 0)
                kt.setFinished(true); 
              if (!MenotteTask.this.isFinished()) {
                if (MenotteTask.this.getDuration() > 0L) {
                  Timestamp now = new Timestamp(System.currentTimeMillis());
                  if (now.getTime() - MenotteTask.this.getStart().getTime() >= MenotteTask.this.getDuration()) {
                    MenotteTask.this.failed(p);
                    cancel();
                  } 
                } 
              } else {
                MenotteTask.this.accomplished(p);
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
      p.sendMessage(ChatColor.RED + "à menotter:");
      for (Jobs.Job j : this.job)
        p.sendMessage(j.getPrefix()); 
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
  
  public static MenotteTask loadData(String key) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    long duration = f.getLong(key + ".GeneralType.duration");
    String desc = f.getString(key + ".GeneralType.desc");
    int count = f.getInt(key + ".MenotteTask.count");
    List<String> jobs = f.getStringList(key + ".MenotteTask.jobs");
    if (duration == 0L)
      return new MenotteTask(count, desc, Jobs.Job.getJobs(jobs)); 
    return new MenotteTask(count, desc, Jobs.Job.getJobs(jobs), duration);
  }
  
  public static void saveData(String key, HashMap<String, Object> infos) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    Object jobs = infos.get("jobs");
    f.set(key + ".MenotteTask.jobs", jobs);
    int count = (Integer) infos.get("count");
    f.set(key + ".MenotteTask.count", count);
  }
  
  public void saveData(String key) {
    super.saveData(key);
    FileConfiguration f = Main.INSTANCE.getConfig();
    f.set(key + ".GeneralType.task", "MenotteTask");
    List<String> listJobs = new ArrayList<>();
    for (Jobs.Job j : this.job)
      listJobs.add(j.getName()); 
    f.set(key + ".MenotteTask.jobs", listJobs);
    f.set(key + ".MenotteTask.count", this.count);
  }
}
