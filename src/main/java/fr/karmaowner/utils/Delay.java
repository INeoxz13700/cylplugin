package fr.karmaowner.utils;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Delay {
  private double secondes;
  
  private Timestamp timer;
  
  private TaskCreator task;
  
  private static HashMap<String, Delay> delays = new HashMap<>();
  
  private String identity;
  
  private String playername;
  
  private Delay(final double secondes, String playername, String ident, CustomRunnable runnable, long DelayBeforeStart, long repeatingTime) {
    this.identity = playername + "§" + ident;
    this.playername = playername;
    this.secondes = secondes;
    delays.put(this.identity, this);
    this.task = new TaskCreator(runnable, true, DelayBeforeStart, repeatingTime);
    this.timer = new Timestamp(System.currentTimeMillis());
    new TaskCreator(new CustomRunnable() {
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if ((now.getTime() - Delay.this.timer.getTime()) >= secondes * 1000.0D) {
              Delay.delays.remove(Delay.this.identity);
              cancel();
            } 
          }
    },  false, 0L, 1L);
  }
  
  private Delay(final double secondes, String playername, String ident) {
    this.identity = playername + "§" + ident;
    this.playername = playername;
    this.secondes = secondes;
    delays.put(this.identity, this);
    this.task = null;
    this.timer = new Timestamp(System.currentTimeMillis());
    new TaskCreator(new CustomRunnable() {
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if ((now.getTime() - Delay.this.timer.getTime()) >= secondes * 1000.0D) {
              Delay.delays.remove(Delay.this.identity);
              cancel();
            } 
          }
        },  false, 0L, 1L);
  }
  
  private Delay(final double secondes, String ident) {
    this.identity = ident;
    this.secondes = secondes;
    delays.put(this.identity, this);
    this.task = null;
    this.timer = new Timestamp(System.currentTimeMillis());
    new TaskCreator(new CustomRunnable() {
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if ((now.getTime() - Delay.this.timer.getTime()) >= secondes * 1000.0D) {
              Delay.delays.remove(Delay.this.identity);
              cancel();
            } 
          }
        },  false, 0L, 1L);
  }
  
  public static void newInstance(double seconds, String playername, String identity, CustomRunnable runnable, long DelayBeforeStart, long repeatingTime) {
    String ident = playername + "§" + identity;
    if (delays.get(ident) == null)
      new Delay(seconds, playername, identity, runnable, DelayBeforeStart, repeatingTime); 
  }
  
  public static void newInstance(double seconds, String playername, String identity) {
    String ident = playername + "§" + identity;
    if (delays.get(ident) == null)
      new Delay(seconds, playername, identity); 
  }
  
  public static void newInstance(double seconds, String identity) {
    if (delays.get(identity) == null)
      new Delay(seconds, identity); 
  }
  
  public static boolean isDelay(String identity, String playername) {
    String ident = playername + "§" + identity;
    if (delays.get(ident) == null)
      return false; 
    return true;
  }
  
  public static double timeElapsedSc(String identity, String playername) {
    if (isDelay(identity, playername)) {
      String ident = playername + "§" + identity;
      return delays.get(ident).secondes - ((System.currentTimeMillis() / 1000.0D) - (delays.get(ident).timer.getTime() / 1000.0D));
    } 
    return -1.0D;
  }
  
  public static double timeElapsedSc(String identity) {
    if (isDelay(identity))
      return delays.get(identity).secondes - ((System.currentTimeMillis() / 1000.0D) - (delays.get(identity).timer.getTime() / 1000.0D));
    return -1.0D;
  }
  
  public static boolean isDelay(String identity) {
    if (delays.get(identity) == null)
      return false; 
    return true;
  }
  
  public static boolean isDelayContains(String identity) {
    for (String ident : delays.keySet()) {
      if (ident.contains(identity))
        return true; 
    } 
    return false;
  }
  
  public static List<String[]> getDelayContainsIdentity(String identity) {
    List<String[]> identities = new ArrayList<>();
    for (String ident : delays.keySet()) {
      if (ident.contains(identity) && ident.contains("§"))
        identities.add(((Delay)delays.get(ident)).identity.split("§")); 
    } 
    return identities;
  }
  
  public static boolean isPlayerExistOnIdentity(String identity, String playername) {
    List<String[]> identities = getDelayContainsIdentity(identity);
    for (String[] ident : identities) {
      if (ident[0].equals(playername))
        return true; 
    } 
    return false;
  }
}
