package fr.karmaowner.gangs;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.utils.RegionUtils;
import fr.karmaowner.utils.TimerUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Rewards {
  private List<Chest> chests;
  
  private ArrayList<fr.karmaowner.gangs.Chest> rewards;
  
  private int seconds;
  
  private Timestamp timer;
  
  private TaskCreator task;
  
  public static final int DURATIONPRINTSECONDS = 1800;
  
  private Capture c;
  
  public Rewards(Capture capture, int seconds, Timestamp timer) {
    this.c = capture;
    List<Block> coffres = RegionUtils.getBlocksByType(Material.CHEST.getId(), this.c.getWorld(), this.c.getRegion());
    this.chests = getChests(coffres);
    this.seconds = seconds;
    if (timer == null) {
      this.timer = new Timestamp(System.currentTimeMillis());
    } else {
      this.timer = timer;
    } 
    this.rewards = new ArrayList<>();
  }
  
  public Timestamp getTimer() {
    return this.timer;
  }
  
  private List<Chest> getChests(List<Block> c) {
    List<Chest> coffres = new ArrayList<>();
    for (Block b : c)
      coffres.add((Chest)b.getState()); 
    return coffres;
  }
  
  public void startTask(final double proportion) {
    this.task = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - Rewards.this.timer.getTime() >= (Rewards.this.seconds * 1000L)) {
              if (Rewards.this.c.getCaptureState() == CaptureState.CATCHED) {
                if (Rewards.this.c.getCaptureOwner() != null) {
                  Rewards.this.c.getGangOwnerData().sendMessageAll(ChatColor.WHITE + "*****" + ChatColor.RED + "Coffre zone " + ChatColor.GOLD + Rewards.this.c.getRegion().getId() + " " + ChatColor.RED + " régénéré " + ChatColor.WHITE + "*****");
                } else {
                  Jobs.Job.MILITAIRE.sendMessageAll(ChatColor.WHITE + "*****" + ChatColor.RED + "Coffre zone " + ChatColor.GOLD + Rewards.this.c.getRegion().getId() + " " + ChatColor.RED + " régénéré " + ChatColor.WHITE + "*****");
                }

                Rewards.this.timer = new Timestamp(System.currentTimeMillis());
                Rewards.this.fillChests(proportion);
              } 
            } else if (Rewards.this.c.getCaptureState() == CaptureState.CATCHED) {
              if ((now.getTime() - Rewards.this.timer.getTime()) % 1800000L >= 1799000L) {
                int left = (int)(Rewards.this.seconds - (now.getTime() - Rewards.this.timer.getTime()) / 1000.0D);
                if (Rewards.this.c.getCaptureOwner() != null) {
                  Rewards.this.c.getGangOwnerData().sendMessageAll(ChatColor.WHITE + "*****" + ChatColor.DARK_RED + "Coffre zone " + ChatColor.RED + Rewards.this.c.getRegion().getId() + " " + ChatColor.RED + " régénéré dans " + TimerUtils.formatString(left) + ChatColor.WHITE + " *****");
                } else {
                  Jobs.Job.MILITAIRE.sendMessageAll(ChatColor.WHITE + "*****" + ChatColor.DARK_RED + "Coffre zone " + ChatColor.RED + Rewards.this.c.getRegion().getId() + " " + ChatColor.RED + " régénéré dans " + TimerUtils.formatString(left) + ChatColor.WHITE + " *****");
                } 
              } 
            } 
          }
        },  false, 0L, 20L);
  }
  
  public void defineRewards(fr.karmaowner.gangs.Chest rewards) {
    this.rewards.add(rewards);
  }
  
  private fr.karmaowner.gangs.Chest getRandomChestRewards() {
    int index = (int)(Math.random() * this.rewards.size());
    return this.rewards.get(index);
  }
  
  private void fillChests(double proportion) {
    clearChests();
    List<Chest> choosenChests = cpyChests();

    int count = (int)(this.chests.size() * proportion);

    for (int i = 0; i < this.chests.size(); i++) {
      if (i <= count) {
        int index = (int)(Math.random() * choosenChests.size());
        choosenChests.remove(index);
      } 
    }

    for (Chest ch : choosenChests) {
      fillChest(ch.getInventory(), getRandomChestRewards().getContents());
    }
  }
  
  private void fillChest(Inventory inv, List<ItemStack> contents) {
    for (ItemStack i : contents)
      inv.addItem(i);
  }
  
  private List<Chest> cpyChests() {
    return new ArrayList<>(this.chests);
  }
  
  public void clearChests() {
    for (Chest c : this.chests)
      c.getInventory().clear(); 
  }
  
  public List<Chest> getChests() {
    return this.chests;
  }
}
