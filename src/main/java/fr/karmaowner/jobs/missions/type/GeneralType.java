package fr.karmaowner.jobs.missions.type;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.missions.MissionType;
import fr.karmaowner.jobs.missions.Missions;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class GeneralType implements MissionType {
  private TaskCreator task;
  
  private boolean finished;
  
  private String desc;
  
  private long duration = 0L;
  
  private Timestamp start;
  
  protected TaskCreator MessageDuration;
  
  public GeneralType(String desc) {
    this.desc = desc;
    this.finished = false;
  }
  
  public GeneralType(String desc, long duration) {
    this(desc);
    this.duration = duration;
  }
  
  public abstract void startTask(Player paramPlayer);
  
  public abstract void informations(String paramString);
  
  public void startMission() {
    this.start = new Timestamp(System.currentTimeMillis());
    this.finished = false;
  }
  
  public void updateDurationMessage(final Player p) {
    if (getDuration() > 0L)
      this.MessageDuration = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              Timestamp now = new Timestamp(System.currentTimeMillis());
              Timestamp restant = new Timestamp(GeneralType.this.getDuration() - now.getTime() - GeneralType.this.getStart().getTime());
              Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "hudmessage 500  " + p.getName() + " 1 " + ChatColor.BLUE + "Durée: " + ChatColor.RED.toString() + restant.getMinutes() + ":" + restant.getSeconds());
            }
          }, false, 0L, 20L); 
  }
  
  public void accomplished(Player p) {
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof Missions) {
      hasGrade grade = (hasGrade)data.selectedJob;
      Missions missions = (Missions)data.selectedJob;
      if (missions.getInProgress() != null) {
        if (this.MessageDuration != null)
          this.MessageDuration.cancelTask(); 
        grade.getGrade().setNbMissions(1);
        data.setMoney(data.getMoney().add(BigDecimal.valueOf(missions.getInProgress().getPrice())));
        grade.getGrade().setXp(missions.getInProgress().getXp());
        p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
        p.sendMessage(ChatColor.GREEN + "Mission accomplie !");
        if (missions.hasRandomMission(missions.getInProgress()) != -1) {
          missions.randomMissions.remove(missions.hasRandomMission(missions.getInProgress()));
        } else if (!missions.finishedMissions.contains(missions.getInProgress().getUUID())) {
          missions.finishedMissions.add(missions.getInProgress().getUUID());
        } 
        missions.setInProgress(null);
      } 
      setTask(null);
    } 
  }
  
  public void failed(Player p) {
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.selectedJob instanceof Missions) {
      Missions missions = (Missions)data.selectedJob;
      if (missions.getInProgress() != null) {
        if (this.MessageDuration != null)
          this.MessageDuration.cancelTask(); 
        p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.natural(0, Note.Tone.F));
        p.sendMessage(ChatColor.RED + "Mission échouée ! Le temps s'est écoulé !");
        missions.setInProgress(null);
      } 
      setTask(null);
    } 
  }
  
  public boolean isFinished() {
    return this.finished;
  }
  
  public void setFinished(boolean finished) {
    this.finished = finished;
  }
  
  public long getDuration() {
    return this.duration;
  }
  
  public void setDuration(long duration) {
    this.duration = duration;
  }
  
  public Timestamp getStart() {
    return this.start;
  }
  
  public void setStart(Timestamp start) {
    this.start = start;
  }
  
  public TaskCreator getTask() {
    return this.task;
  }
  
  public void setTask(TaskCreator task) {
    this.task = task;
  }
  
  public String getDesc() {
    return this.desc;
  }
  
  public void setDesc(String desc) {
    this.desc = desc;
  }
  
  public static GeneralType loadData(String key) {
    String taskName = Main.INSTANCE.getConfig().getString(key + ".GeneralType.task");
    if (taskName.equalsIgnoreCase("KillTask"))
      return KillTask.loadData(key); 
    if (taskName.equalsIgnoreCase("DefendTask"))
      return DefendTask.loadData(key); 
    if (taskName.equalsIgnoreCase("FouilleTask"))
      return FouilleTask.loadData(key); 
    if (taskName.equalsIgnoreCase("MenotteTask"))
      return MenotteTask.loadData(key); 
    if (taskName.equalsIgnoreCase("AmendeTask"))
      return AmendeTask.loadData(key); 
    return null;
  }
  
  public static void saveData(String key, HashMap<String, Object> infos) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    long duration = (Long) infos.get("duration");
    f.set(key + ".GeneralType.desc", infos.get("desc"));
    if (duration > 0L)
      f.set(key + ".GeneralType.duration", duration);
    String taskName = (String)infos.get("task");
    f.set(key + ".GeneralType.task", taskName);
    if (taskName.equalsIgnoreCase("KillTask")) {
      KillTask.saveData(key, infos);
    } else if (taskName.equalsIgnoreCase("DefendTask")) {
      DefendTask.saveData(key, infos);
    } else if (taskName.equalsIgnoreCase("FouilleTask")) {
      FouilleTask.saveData(key, infos);
    } else if (taskName.equalsIgnoreCase("MenotteTask")) {
      MenotteTask.saveData(key, infos);
    } else if (taskName.equalsIgnoreCase("AmendeTask")) {
      AmendeTask.saveData(key, infos);
    } 
  }
  
  public void saveData(String key) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    f.set(key + ".GeneralType.desc", this.desc);
    if (this.duration > 0L)
      f.set(key + ".GeneralType.duration", this.duration);
  }
}
