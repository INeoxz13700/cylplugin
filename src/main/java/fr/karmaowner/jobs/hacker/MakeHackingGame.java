package fr.karmaowner.jobs.hacker;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.jobs.Jobs;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class MakeHackingGame {
  private Inventory inv;
  
  private Player p;
  
  private Hacker h;
  
  private int slots;
  
  private boolean finished;
  
  public abstract void winGame(ItemStack paramItemStack, int paramInt);
  
  public abstract String HackingType();
  
  public MakeHackingGame(int slots, String nameInventory, Player p, Hacker h) {
    this.slots = slots;
    this.inv = Main.INSTANCE.getServer().createInventory(null, slots, nameInventory);
    this.p = p;
    setHacker(h);
  }
  
  public void end() {
    setFinished(true);
    if (getHacker() != null) {
      if (getHacker().getTask() != null) {
        getHacker().getTask().cancelTask();
        getHacker().setTask(null);
      } 
      getHacker().setGame(null);
    } 
    if (getPlayer() != null)
      getPlayer().closeInventory(); 
  }
  
  public void start() {
    Location l = this.p.getLocation();
    int x = (int)l.getX();
    int y = (int)l.getY();
    int z = (int)l.getZ();
    for (Jobs.Job j : Jobs.Job.getJobs(Arrays.asList("policier", "gign", "gendarme")))
      j.sendMessageAll("§f[" + ChatColor.BLUE + "Force de l'ordre§f]", ChatColor.DARK_AQUA + HackingType() + ChatColor.BLUE + "x=" + x + " y=" + y + " z=" + z); 
    this.p.openInventory(this.inv);
  }
  
  public void reset() {
    setFinished(false);
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public Inventory getInventory() {
    return this.inv;
  }
  
  public String getTitle() {
    return this.inv.getTitle();
  }
  
  public boolean getFinished() {
    return this.finished;
  }
  
  public void setFinished(boolean b) {
    this.finished = b;
  }
  
  public Hacker getHacker() {
    return this.h;
  }
  
  public void setHacker(Hacker h) {
    this.h = h;
  }
  
  public int getSlots() {
    return this.slots;
  }
}
