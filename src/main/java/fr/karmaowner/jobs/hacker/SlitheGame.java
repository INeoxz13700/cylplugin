package fr.karmaowner.jobs.hacker;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.jobs.Hacker;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class SlitheGame extends MakeHackingGame {
  private Move m;
  
  private int maxLine;
  
  private int maxColumn;
  
  private HashMap<Movement, TaskCreator> listTask = new HashMap<>();
  
  public enum Move {
    LEFT, RIGHT, NONE;
  }
  
  public SlitheGame(int slots, String nameInventory, Player p, Hacker h) {
    super(slots, nameInventory, p, h);
    this.m = Move.NONE;
    this.maxColumn = 9;
    this.maxLine = slots / this.maxColumn;
  }
  
  public TaskCreator getTasks(Movement m) {
    for (Map.Entry<Movement, TaskCreator> v : this.listTask.entrySet()) {
      if (((Movement)v.getKey()).getPosition() == m.getPosition() && ((Movement)v.getKey()).getType() == m.getType())
        return v.getValue(); 
    } 
    return null;
  }
  
  public HashMap<Movement, TaskCreator> getTasks() {
    return this.listTask;
  }
  
  public void addItem(int slot, ItemStack item) {
    getInventory().setItem(slot, item);
  }
  
  public void end() {
    for (TaskCreator t : this.listTask.values())
      t.cancelTask(); 
    super.end();
  }
  
  public String getTitle() {
    return getInventory().getTitle();
  }
  
  public void moveLineLeft(int line, long tick) {
    if (line >= 1 && line <= this.maxLine) {
      final int pos = (line - 1) * 9;
      TaskCreator task = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              ItemStack[] items = (ItemStack[])SlitheGame.this.getInventory().getContents().clone();
              for (int i = pos; i <= pos + 8; i++) {
                int newLoc = (i % 9 == 0) ? (pos + 8) : (i - 1);
                if (items[i] != null)
                  SlitheGame.this.getInventory().setItem(newLoc, items[i]); 
              } 
            }
          }, false, 0L, tick);
      this.listTask.put(MovementBuilder.build().setRow().setLeft().setPosition(line).getMovement(), task);
    } else {
      Main.Log("dépasse le nombre de ligne autorisé :" + line + " >" + this.maxLine);
    } 
  }
  
  public void moveLineRight(int line, long tick) {
    if (line >= 1 && line <= this.maxLine) {
      final int pos = (line - 1) * 9;
      TaskCreator task = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              ItemStack[] items = (ItemStack[])SlitheGame.this.getInventory().getContents().clone();
              for (int i = pos; i <= pos + 8; i++) {
                int newLoc = (i % (pos + 8) == 0) ? pos : (i + 1);
                if (items[i] != null)
                  SlitheGame.this.getInventory().setItem(newLoc, items[i]); 
              } 
            }
          }, false, 0L, tick);
      this.listTask.put(MovementBuilder.build().setRow().setRight().setPosition(line).getMovement(), task);
    } else {
      Main.Log("dépasse le nombre de ligne autorisé :" + line + " >" + this.maxLine);
    } 
  }
  
  public void moveColumnLeft(int column, long tick) {
    if (column >= 1 && column <= this.maxColumn) {
      final int pos = column - 1;
      TaskCreator task = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              ItemStack[] items = (ItemStack[])SlitheGame.this.getInventory().getContents().clone();
              int max = pos + 9 * (SlitheGame.this.maxLine - 1);
              for (int i = pos; i <= max; i += SlitheGame.this.maxColumn) {
                int newLoc = (i == pos) ? max : (i - SlitheGame.this.maxColumn);
                if (items[i] != null)
                  SlitheGame.this.getInventory().setItem(newLoc, items[i]); 
              } 
            }
          }, false, 0L, tick);
      this.listTask.put(MovementBuilder.build().setColumn().setLeft().setPosition(column).getMovement(), task);
    } else {
      Main.Log("dépasse le nombre de ligne autorisé :" + column + " >" + this.maxColumn);
    } 
  }
  
  public void moveColumnRight(int column, long tick) {
    if (column >= 1 && column <= this.maxColumn) {
      final int pos = column - 1;
      TaskCreator task = new TaskCreator(new CustomRunnable() {
            public void customRun() {
              ItemStack[] items = (ItemStack[])SlitheGame.this.getInventory().getContents().clone();
              int max = pos + 9 * (SlitheGame.this.maxLine - 1);
              for (int i = pos; i <= max; i += SlitheGame.this.maxColumn) {
                int newLoc = (i == max) ? pos : (i + SlitheGame.this.maxColumn);
                if (items[i] != null)
                  SlitheGame.this.getInventory().setItem(newLoc, items[i]); 
              } 
            }
          }, false, 0L, tick);
      this.listTask.put(MovementBuilder.build().setColumn().setRight().setPosition(column).getMovement(), task);
    } else {
      Main.Log("dépasse le nombre de ligne autorisé :" + column + " >" + this.maxColumn);
    } 
  }
}
