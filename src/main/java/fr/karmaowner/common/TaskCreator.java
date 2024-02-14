package fr.karmaowner.common;

import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskCreator {
  private CustomRunnable run;
  
  private BukkitRunnable WhenFinishedTask;
  
  private boolean running;
  
  private boolean asyn;
  
  private String id;
  
  private static HashMap<String, CustomRunnable> tasks = new HashMap<>();
  
  private long DelayBeforeStart;
  
  private long RepeatingTime = -1L;
  
  private TaskCreator(CustomRunnable runnable, boolean asyn) {
    this.asyn = asyn;
    this.run = runnable;
    this.running = true;
  }
  
  private TaskCreator(CustomRunnable runnable, boolean asyn, String id) {
    this(runnable, asyn);
    this.id = id;
    tasks.put(id, runnable);
  }
  
  public TaskCreator(CustomRunnable runnable, boolean asyn, long DelayBeforeStart) {
    this(runnable, asyn);
    this.DelayBeforeStart = DelayBeforeStart;
    start();
  }
  
  public TaskCreator(CustomRunnable runnable, boolean asyn, long DelayBeforeStart, String id) {
    this(runnable, asyn, id);
    this.DelayBeforeStart = DelayBeforeStart;
    start();
  }
  
  public TaskCreator(CustomRunnable runnable, boolean asyn, long DelayBeforeStart, long RepeatingTime) {
    this(runnable, asyn);
    this.DelayBeforeStart = DelayBeforeStart;
    this.RepeatingTime = RepeatingTime;
    start();
  }
  
  public TaskCreator(CustomRunnable runnable, boolean asyn, long DelayBeforeStart, long RepeatingTime, String id) {
    this(runnable, asyn, id);
    this.DelayBeforeStart = DelayBeforeStart;
    this.RepeatingTime = RepeatingTime;
    start();
  }
  
  public CustomRunnable getRunnable() {
    return this.run;
  }
  
  public String getId() {
    return this.id;
  }
  
  public BukkitRunnable getWhenFinishedTask() {
    return this.WhenFinishedTask;
  }
  
  public void setWhenFinishedTask(CustomRunnable r) {
    this.WhenFinishedTask = r;
  }
  
  public void start() {
    if (this.RepeatingTime == -1L) {
      if (!this.asyn) {
        this.run.runTaskLater(Main.INSTANCE, this.DelayBeforeStart);
      } else {
        this.run.runTaskLaterAsynchronously(Main.INSTANCE, this.DelayBeforeStart);
      } 
    } else if (!this.asyn) {
      this.run.runTaskTimer(Main.INSTANCE, this.DelayBeforeStart, this.RepeatingTime);
    } else {
      this.run.runTaskTimerAsynchronously(Main.INSTANCE, this.DelayBeforeStart, this.RepeatingTime);
    } 
  }
  
  public static CustomRunnable getSavedTask(String id) {
    if (tasks.containsKey(id))
      return tasks.get(id); 
    return null;
  }
  
  public static void removeSavedTask(String id) {
    if (tasks.containsKey(id))
      tasks.remove(id); 
  }
  
  public void attachObject(Object obj) {
    this.run.setAttached(obj);
  }
  
  public Object getObj() {
    return this.run.getAttached();
  }
  
  public long getDelay() {
    return this.DelayBeforeStart;
  }
  
  public long getRepeatingTime() {
    return this.RepeatingTime;
  }
  
  public boolean getRunningStatus() {
    return this.running;
  }
  
  public void WhenTaskFinished(CustomRunnable r) {
    setWhenFinishedTask(r);
    new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (!TaskCreator.this.getRunningStatus()) {
              if (TaskCreator.this.WhenFinishedTask != null)
                TaskCreator.this.WhenFinishedTask.runTask(Main.INSTANCE);
              cancel();
            } 
          }
        },  false, 0L, 20L);
  }
  
  public void cancelTask() {
    if (this.RepeatingTime != -1L)
      this.run.cancel(); 
    this.running = false;
  }
}
