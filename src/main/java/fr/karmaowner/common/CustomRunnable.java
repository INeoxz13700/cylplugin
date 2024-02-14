package fr.karmaowner.common;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class CustomRunnable extends BukkitRunnable {
  private volatile boolean cancelled = false;
  
  private volatile Object attached;
  
  public abstract void customRun();
  
  public void run() {
    try {
      customRun();
    } catch (Exception e) {
      cancel();
      Main.Log("Erreur détecté:\n");
      String errors = "";
      for (StackTraceElement el : e.getStackTrace())
        errors = errors + el + " "; 
      Main.Log(errors);
      Main.essentials.getUser("Ozmentv").addMail("Erreur détecté:\n" + errors);
    } 
  }
  
  public void cancel() {
    this.cancelled = true;
    super.cancel();
  }
  
  public void setAttached(Object o) {
    this.attached = o;
  }
  
  public Object getAttached() {
    return this.attached;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
}
