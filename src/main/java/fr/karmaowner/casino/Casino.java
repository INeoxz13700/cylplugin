package fr.karmaowner.casino;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Casino implements Listener {
  public State s = State.WAITING;
  
  public static String RGNAME = "Casino";
  
  public Inventory inv;
  
  public int nbPlayers;
  
  private int WaitingTime;
  
  private boolean waitEverybody;
  
  public Bet b;
  
  public boolean isBetActivated;
  
  public ArrayList<Player> listPlayers;
  
  public static ArrayList<Casino> waitingGames = new ArrayList<>();
  
  private TaskCreator waiting;
  
  private TaskCreator game;
  
  public enum State {
    STARTED, WAITING, END;
  }
  
  public enum GameType {
    Jackpot(false),
    Roulette(true),
    Tictactoc(true);
    
    private boolean isBetActivated;
    
    GameType(boolean isBetActivated) {
      this.isBetActivated = isBetActivated;
    }
    
    public boolean getBetStatut() {
      return this.isBetActivated;
    }
  }
  
  public void desktroyGame() {
    waitingGames.remove(this);
    if (this.waiting != null)
      this.waiting.cancelTask(); 
  }
  
  public void setTaskGame(TaskCreator task) {
    this.game = task;
  }
  
  public TaskCreator getTaskGame() {
    return this.game;
  }
  
  public String toString() {
    return "-" + this.s + " : " + this.listPlayers.size() + "/" + this.nbPlayers;
  }
  
  public Casino(int nbPlayers, int WaitingTime, boolean waitEverybody, boolean isBetActivated) {
    this.nbPlayers = nbPlayers;
    this.WaitingTime = WaitingTime;
    this.waitEverybody = waitEverybody;
    this.b = null;
    this.game = null;
    this.isBetActivated = isBetActivated;
  }
  
  public void endGame() {
    this.s = State.END;
    if (this.game != null)
      this.game.cancelTask(); 
    for (Player p : this.listPlayers) {
      (PlayerData.getPlayerData(p.getName())).c = null;
      p.closeInventory();
    } 
    waitingGames.remove(this);
  }
  
  public void TaskWaiting() {
    long Delay = 0L;
    long RepeatingTime = 100L;
    this.waiting = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (Casino.this.nbPlayers == Casino.this.listPlayers.size() || (!Casino.this.waitEverybody && Casino.this.listPlayers.size() > 0)) {
              if (--Casino.this.WaitingTime <= 0 || Casino.this.waitEverybody) {
                Casino.this.startGame();
                Casino.this.waiting.cancelTask();
              } else {
                for (Player p : Casino.this.listPlayers)
                  p.sendMessage("§6Temps restant avant le début de la partie: §e" + Casino.this.WaitingTime); 
              } 
            } else {
              for (Player p : Casino.this.listPlayers)
                p.sendMessage(ChatColor.DARK_AQUA + "Recherche de partenaires en cours..."); 
            } 
          }
        },  false, Delay, RepeatingTime);
  }
  
  public void NotificatePlayers() {
    for (Player p : this.listPlayers)
      p.sendMessage("nombre de joueurs dans la partie : " + this.listPlayers.size() + "/" + this.nbPlayers); 
  }
  
  public void NotificatePlayers(String msg) {
    for (Player p : this.listPlayers)
      p.sendMessage(msg); 
  }
  
  public static boolean hasEnoughMoneyForBet(String game, Player p) {
    if (!GameType.valueOf(game).getBetStatut())
      return true; 
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (data.getMoney().compareTo(BigDecimal.valueOf(100.0D)) == 1 || data
      .getMoney().compareTo(BigDecimal.valueOf(100.0D)) == 0)
      return true; 
    return false;
  }
  
  public static void joinParty(String GameType, Player p) {
    if ((PlayerData.getPlayerData(p.getName())).c == null) {
      if (!hasEnoughMoneyForBet(GameType, p)) {
        p.sendMessage(ChatColor.RED + "Vous n'avez pas de sous pour participer à la mise. Impossible de rejoindre le jeu.");
        return;
      } 
      for (Casino c : waitingGames) {
        if (c.s == State.WAITING) {
          if (GameType.equals("Tictactoc") && c instanceof TictactocGame) {
            c.listPlayers.add(p);
            c.NotificatePlayers();
            (PlayerData.getPlayerData(p.getName())).c = c;
            return;
          } 
          if (GameType.equals("Jackpot") && c instanceof Jackpot) {
            FileConfiguration cf = Main.INSTANCE.getConfig();
            int necessaryItemId = cf.getInt("Casino.jackpot.necessary-item.id");
            byte type = Byte.parseByte(cf.getString("Casino.jackpot.necessary-item.byte"));
            int quantity = cf.getInt("Casino.jackpot.necessary-item.quantity");
            if (!ItemUtils.removeItemFromInventory(necessaryItemId, type, quantity, p)) {
              String msg = MessageUtils.getMessageFromConfig("need-item");
              msg = msg.replaceAll("%quantity%", "" + quantity);
              msg = msg.replaceAll("%itemname%", Item.getById(necessaryItemId).getName());
              msg = msg.replaceAll("&", "§");
              MessageUtils.sendMessage((CommandSender)p, msg);
              return;
            } 
            c.listPlayers.add(p);
            c.NotificatePlayers();
            (PlayerData.getPlayerData(p.getName())).c = c;
            return;
          } 
          if (GameType.equals("Roulette") && c instanceof RouletteGame) {
            c.listPlayers.add(p);
            c.NotificatePlayers();
            (PlayerData.getPlayerData(p.getName())).c = c;
            return;
          } 
        } 
      } 
      ArrayList<Player> listPlayers = new ArrayList<>();
      listPlayers.add(p);
      if (GameType.equals("Tictactoc")) {
        TictactocGame t = new TictactocGame(p);
        t.listPlayers = listPlayers;
        waitingGames.add(t);
        t.NotificatePlayers();
        t.TaskWaiting();
        (PlayerData.getPlayerData(p.getName())).c = t;
      } else if (GameType.equals("Jackpot")) {
        FileConfiguration cf = Main.INSTANCE.getConfig();
        int necessaryItemId = cf.getInt("Casino.jackpot.necessary-item.id");
        byte type = Byte.parseByte(cf.getString("Casino.jackpot.necessary-item.byte"));
        int quantity = cf.getInt("Casino.jackpot.necessary-item.quantity");
        if (!ItemUtils.removeItemFromInventory(necessaryItemId, type, quantity, p)) {
          String msg = MessageUtils.getMessageFromConfig("need-item");
          msg = msg.replaceAll("%quantity%", "" + quantity);
          msg = msg.replaceAll("%itemname%", Item.getById(necessaryItemId).getName());
          msg = msg.replaceAll("&", "§");
          MessageUtils.sendMessage((CommandSender)p, msg);
          return;
        } 
        Jackpot j = new Jackpot(p);
        j.listPlayers = listPlayers;
        waitingGames.add(j);
        j.NotificatePlayers();
        j.TaskWaiting();
        (PlayerData.getPlayerData(p.getName())).c = j;
      } else if (GameType.equals("Roulette")) {
        RouletteGame r = new RouletteGame(p);
        r.listPlayers = listPlayers;
        waitingGames.add(r);
        r.NotificatePlayers();
        r.TaskWaiting();
        (PlayerData.getPlayerData(p.getName())).c = r;
      } 
    } else {
      p.sendMessage(ChatColor.DARK_RED + "Vous êtes déjà dans une partie.");
    } 
  }
  
  public static void StopWaiting(Player p) {
    Casino c = getGame(p);
    if (c != null) {
      if (c.s == State.WAITING) {
        if (c.listPlayers.size() == 1) {
          c.desktroyGame();
          c = null;
        } else {
          c.listPlayers.remove(p);
          c.NotificatePlayers();
        } 
        (PlayerData.getPlayerData(p.getName())).c = null;
        p.sendMessage("§cVous venez de quitter la partie.");
      } else {
        p.sendMessage("§aLe jeu a déjà débuté, vous ne pouvez pas quitter la partie.");
      } 
    } else {
      p.sendMessage("§aAucune partie en cours.");
    } 
  }
  
  public static Casino getGame(Player p) {
    for (Casino c : waitingGames) {
      for (Player p1 : c.listPlayers) {
        if (p1.equals(p))
          return c; 
      } 
    } 
    return null;
  }
  
  public void addItemInventory(Material material, int id, int Slot, String name) {
    ItemStack item = new ItemStack(material, 1, (short)(byte)id);
    ItemMeta i = item.getItemMeta();
    i.setDisplayName(name);
    item.setItemMeta(i);
    this.inv.setItem(Slot, item);
  }
  
  public void closeInventory() {
    for (Player p : this.listPlayers)
      p.closeInventory(); 
  }
  
  public void openInventory() {
    if (this.s == State.STARTED)
      for (Player p : this.listPlayers) {
        if (p.getOpenInventory() != null && 
          !p.getOpenInventory().getTopInventory().getName().equals(this.inv.getName()))
          p.openInventory(this.inv); 
      }  
  }
  
  public static void listParty(Player p) {
    p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "--------------------------------");
    p.sendMessage(ChatColor.DARK_RED + "| Nombre de partie : " + waitingGames.size());
    for (Casino c : waitingGames)
      p.sendMessage(c.toString()); 
    p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "--------------------------------");
  }
  
  abstract void startGame();
}
