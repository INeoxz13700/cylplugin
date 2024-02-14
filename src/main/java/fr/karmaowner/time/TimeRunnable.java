package fr.karmaowner.time;

import fr.karmaowner.common.CustomRunnable;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeRunnable extends CustomRunnable {
  private JavaPlugin plugin;
  
  public TimeRunnable(JavaPlugin plugin) {
    this.plugin = plugin;
  }
  
  public void customRun() {
    Date date = new Date();
    int hours = date.getHours();
    int minutes = date.getMinutes();
    long time = 0L;
    if (date.getMonth() >= 3 && date.getMonth() < 10) {
      if ((hours >= 6 && hours <= 20) || (hours == 21 && minutes <= 30)) {
        time += 770L * (hours - 6) + 13L * minutes;
      } else {
        time += 12000L + 1410L * ((hours != 23 && hours != 22) ? (hours + 24 - 21) : (hours - 21)) + 23L * minutes;
      } 
    } else if (hours >= 8 && hours <= 18) {
      time += 1200L * (hours - 8) + 20L * minutes;
    } else {
      time += 12000L + 857L * ((hours < 19 || hours > 23) ? (hours + 24 - 18) : (hours - 18)) + 14L * minutes;
    } 
    this.plugin.getServer().getWorld("cyl").setTime(time);
  }
}
