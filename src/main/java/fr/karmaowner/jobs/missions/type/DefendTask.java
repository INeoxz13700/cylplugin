package fr.karmaowner.jobs.missions.type;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import java.awt.Point;
import java.sql.Timestamp;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DefendTask extends GeneralType {
  private Vector firstPoint;
  
  private Vector secondPoint;
  
  private long timeDefend;
  
  private Timestamp startTimeDefend;
  
  public DefendTask(String desc, Vector first, Vector second, long timeDefend) {
    super(desc);
    setFirstPoint(first);
    setSecondPoint(second);
    this.timeDefend = timeDefend;
  }
  
  public DefendTask(String desc, long duration, Vector first, Vector second, long timeDefend) {
    super(desc, duration);
    setFirstPoint(first);
    setSecondPoint(second);
    this.timeDefend = timeDefend;
  }
  
  public void startMission() {
    super.startMission();
    this.startTimeDefend = new Timestamp(System.currentTimeMillis());
  }
  
  public void startTask(final Player p) {
    startMission();
    informations(p.getName());
    updateDurationMessage(p);
    setTask(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              if (Bukkit.getPlayerExact(p.getName()) == null) {
                DefendTask.this.failed(p);
                cancel();
                return;
              } 
              Location r1 = p.getLocation();
              Timestamp now = new Timestamp(System.currentTimeMillis());
              int x1 = Math.min((int)Math.abs(DefendTask.this.firstPoint.getX()), (int)Math.abs(DefendTask.this.secondPoint.getX()));
              int x2 = Math.max((int)Math.abs(DefendTask.this.firstPoint.getX()), (int)Math.abs(DefendTask.this.secondPoint.getX()));
              int y1 = Math.min((int)Math.abs(DefendTask.this.firstPoint.getZ()), (int)Math.abs(DefendTask.this.secondPoint.getZ()));
              int y2 = Math.max((int)Math.abs(DefendTask.this.firstPoint.getZ()), (int)Math.abs(DefendTask.this.secondPoint.getZ()));
              Point bottomLeft = new Point(x1, y1);
              Point topRight = new Point(x2, y2);
              Point bottomRight = new Point(topRight.x, bottomLeft.y);
              Point topLeft = new Point(bottomLeft.x, topRight.y);
              Point pp = new Point((int)Math.abs(p.getLocation().getX()), (int)Math.abs(p.getLocation().getZ()));
              boolean Xaxe = (pp.x >= bottomLeft.x && pp.x <= bottomRight.x);
              boolean Yaxe = (pp.y >= bottomLeft.y && pp.y <= topLeft.y);
              if (!Xaxe || !Yaxe) {
                DefendTask.this.startTimeDefend = new Timestamp(System.currentTimeMillis());
              } else {
                DefendTask.this.setStart(new Timestamp(System.currentTimeMillis()));
              } 
              if (now.getTime() - DefendTask.this.startTimeDefend.getTime() >= DefendTask.this.timeDefend) {
                DefendTask.this.accomplished(p);
                cancel();
              } else if (DefendTask.this.getDuration() > 0L && 
                now.getTime() - DefendTask.this.getStart().getTime() >= DefendTask.this.getDuration()) {
                DefendTask.this.failed(p);
                cancel();
              } 
            }
          }, false, 0L, 20L));
  }
  
  public void informations(String playername) {
    Player p = Bukkit.getPlayerExact(playername);
    if (p != null) {
      p.sendMessage(ChatColor.GOLD + ChatColor.UNDERLINE.toString() + getDesc());
      p.sendMessage(ChatColor.WHITE + "--------------");
      p.sendMessage(ChatColor.RED + "Coordonnées");
      int x1 = (int)getFirstPoint().getX(), z1 = (int)getFirstPoint().getZ();
      int x2 = (int)getSecondPoint().getX(), z2 = (int)getSecondPoint().getZ();
      p.sendMessage(ChatColor.DARK_RED + "x=" + x1 + " z=" + z1);
      p.sendMessage(ChatColor.DARK_RED + "x=" + x2 + " z=" + z2);
      p.sendMessage(ChatColor.LIGHT_PURPLE + "pour obtenir des informations /jobs missions about  ou /jbs ma");
    } 
  }
  
  public void updateDurationMessage(final Player p) {
    if (getDuration() > 0L) {
      this.MessageDuration = new TaskCreator(new CustomRunnable() {
            private long timer = System.currentTimeMillis();
            
            public void customRun() {
              Timestamp now = new Timestamp(System.currentTimeMillis());
              Timestamp restant = new Timestamp(DefendTask.this.getDuration() - now.getTime() - DefendTask.this.getStart().getTime());
              Timestamp timeLeftDefend = new Timestamp(DefendTask.this.timeDefend - now.getTime() - DefendTask.this.startTimeDefend.getTime());
              if (now.getTime() - this.timer >= 1500L) {
                this.timer = System.currentTimeMillis();
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "hudmessage 500 " + p.getName() + " 1 " + ChatColor.BLUE + "Durée: " + ChatColor.RED.toString() + restant.getMinutes() + ":" + restant.getSeconds() + ChatColor.AQUA
                    .toString() + "défense: " + ChatColor.DARK_AQUA.toString() + " " + timeLeftDefend.getMinutes() + ":" + timeLeftDefend.getSeconds());
              } 
            }
          }, false, 0L, 20L);
    } else {
      this.MessageDuration = new TaskCreator(new CustomRunnable() {
            private long timer = System.currentTimeMillis();
            
            public void customRun() {
              Timestamp now = new Timestamp(System.currentTimeMillis());
              Timestamp timeLeftDefend = new Timestamp(DefendTask.this.timeDefend - now.getTime() - DefendTask.this.startTimeDefend.getTime());
              if (now.getTime() - this.timer >= 1500L) {
                this.timer = System.currentTimeMillis();
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "hudmessage 500 " + p.getName() + " 1 défense: " + ChatColor.DARK_AQUA.toString() + " " + timeLeftDefend.getMinutes() + ":" + timeLeftDefend.getSeconds());
              } 
            }
          }, false, 0L, 20L);
    } 
  }
  
  public Vector getFirstPoint() {
    return this.firstPoint;
  }
  
  public void setFirstPoint(Vector firstPoint) {
    this.firstPoint = firstPoint;
  }
  
  public Vector getSecondPoint() {
    return this.secondPoint;
  }
  
  public void setSecondPoint(Vector secondPoint) {
    this.secondPoint = secondPoint;
  }
  
  public long getTimeDefend() {
    return this.timeDefend;
  }
  
  public void setTimeDefend(long timeDefend) {
    this.timeDefend = timeDefend;
  }
  
  public static DefendTask loadData(String key) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    long duration = f.getLong(key + ".GeneralType..duration");
    String desc = f.getString(key + ".GeneralType..desc");
    int x1 = f.getInt(key + ".DefendTask..firstPoint.x");
    int y1 = f.getInt(key + ".DefendTask..firstPoint.y");
    int z1 = f.getInt(key + ".DefendTask..firstPoint.z");
    int x2 = f.getInt(key + ".DefendTask..secondPoint.x");
    int y2 = f.getInt(key + ".DefendTask..secondPoint.y");
    int z2 = f.getInt(key + ".DefendTask..secondPoint.z");
    long timeDefend = f.getLong(key + ".DefendTask..timeDefend");
    if (duration == 0L)
      return new DefendTask(desc, new Vector(x1, y1, z1), new Vector(x2, y2, z2), timeDefend); 
    return new DefendTask(desc, duration, new Vector(x1, y1, z1), new Vector(x2, y2, z2), timeDefend);
  }
  
  public static void saveData(String key, HashMap<String, Object> infos) {
    FileConfiguration f = Main.INSTANCE.getConfig();
    Vector firstPoint = (Vector)infos.get("firstPoint");
    Vector secondPoint = (Vector)infos.get("secondPoint");
    f.set(key + ".DefendTask.firstPoint.x", firstPoint.getBlockX());
    f.set(key + ".DefendTask.firstPoint.y", firstPoint.getBlockY());
    f.set(key + ".DefendTask.firstPoint.z", firstPoint.getBlockZ());
    f.set(key + ".DefendTask.secondPoint.x", secondPoint.getBlockX());
    f.set(key + ".DefendTask.secondPoint.y", secondPoint.getBlockY());
    f.set(key + ".DefendTask.secondPoint.z", secondPoint.getBlockZ());
    f.set(key + ".DefendTask.timeDefend", ((Long) infos.get("timeDefend")).longValue());
  }
  
  public void saveData(String key) {
    super.saveData(key);
    FileConfiguration f = Main.INSTANCE.getConfig();
    f.set(key + ".GeneralType.task", "DefendTask");
    f.set(key + ".DefendTask.firstPoint.x", this.firstPoint.getBlockX());
    f.set(key + ".DefendTask.firstPoint.y", this.firstPoint.getBlockY());
    f.set(key + ".DefendTask.firstPoint.z", this.firstPoint.getBlockZ());
    f.set(key + ".DefendTask.secondPoint.x", this.secondPoint.getBlockX());
    f.set(key + ".DefendTask.secondPoint.y", this.secondPoint.getBlockY());
    f.set(key + ".DefendTask.secondPoint.z", this.secondPoint.getBlockZ());
    f.set(key + ".DefendTask.timeDefend", this.timeDefend);
  }
}
