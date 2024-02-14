package fr.karmaowner.casino;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class Jackpot extends Casino {
  private Player p;
  
  private int time = 5;
  
  private int timeLeft = 5;
  
  private int waitTime = 10;
  
  int next = -1;
  
  private Material[] randomItems = new Material[] { Material.APPLE, Material.GOLDEN_APPLE, Material.DIAMOND, Material.TNT };
  
  private int[] recompenses = new int[] { 500, 1000, 2000, 3000 };
  
  private ArrayList<Material> listItems = new ArrayList<>();
  
  public Jackpot(Player p) {
    super(1, 0, true, Casino.GameType.Jackpot.getBetStatut());
    this.p = p;
    this.inv = Bukkit.createInventory(null, InventoryType.DISPENSER, ChatColor.BLUE + "Jackpot");
    int i;
    for (i = 0; i < 18; i++) {
      int random = (int)(Math.random() * this.randomItems.length);
      this.listItems.add(this.randomItems[random]);
    } 
    for (i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        int item = i * 6 + j;
        int slot = i * 3 + j;
        addItemInventory(this.listItems.get(item), 0, slot, "");
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (e.getInventory().getName().equals(this.inv.getName()))
      e.setCancelled(true); 
  }
  
  public void startGame() {
    if (this.s == Casino.State.WAITING) {
      this.s = Casino.State.STARTED;
      addRandomItemInv();
    } 
  }
  
  public int CheckWinner() {
    ItemStack slot1 = this.inv.getItem(3);
    ItemStack slot2 = this.inv.getItem(4);
    ItemStack slot3 = this.inv.getItem(5);
    for (int i = 0; i < this.randomItems.length; i++) {
      if (slot1.getType() == this.randomItems[i] && slot2
        .getType() == this.randomItems[i] && slot3
        .getType() == this.randomItems[i])
        return i; 
    } 
    return -1;
  }
  
  public void endGame() {
    if (this.s == Casino.State.END) {
      int m = CheckWinner();
      if (m != -1) {
        int recompense = this.recompenses[m];
        this.p.sendMessage("§aFélicitation: Vous avez remporté §6" + recompense + "€");
        PlayerData data = PlayerData.getPlayerData(this.p.getName());
        data.setMoney(data.getMoney().add(BigDecimal.valueOf(recompense)));
      } else {
        this.p.sendMessage("§cVous n'avez rien gagné !");
      } 
      this.p.sendMessage("§cFin de la partie.");
      super.endGame();
    } 
  }
  
  public void addRandomItemInv() {
    setTaskGame(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              Jackpot.this.openInventory();
              if (--Jackpot.this.timeLeft <= 0) {
                Jackpot.this.next++;
                Jackpot.this.timeLeft = Jackpot.this.time;
              } 
              if (Jackpot.this.next < 0)
                for (int j = 0; j < 3; j++) {
                  int item = (0 + j + Jackpot.this.time - Jackpot.this.timeLeft) % 18;
                  int slot = 0 + j * 3;
                  Jackpot.this.addItemInventory(Jackpot.this.listItems.get(item), 0, slot, "");
                }  
              if (Jackpot.this.next < 1)
                for (int j = 0; j < 3; j++) {
                  int item = (6 + j + Jackpot.this.time - Jackpot.this.timeLeft) % 18;
                  int slot = 1 + j * 3;
                  Jackpot.this.addItemInventory(Jackpot.this.listItems.get(item), 0, slot, "");
                }  
              if (Jackpot.this.next < 2) {
                for (int j = 0; j < 3; j++) {
                  int item = (12 + j + Jackpot.this.time - Jackpot.this.timeLeft) % 18;
                  int slot = 2 + j * 3;
                  Jackpot.this.addItemInventory(Jackpot.this.listItems.get(item), 0, slot, "");
                } 
              } else if (--Jackpot.this.waitTime <= 0) {
                Jackpot.this.s = Casino.State.END;
                Jackpot.this.endGame();
              } 
            }
          },false, 0L, 20L));
  }
  
  public String toString() {
    return ChatColor.RED + "Jackpot" + super.toString();
  }
}
