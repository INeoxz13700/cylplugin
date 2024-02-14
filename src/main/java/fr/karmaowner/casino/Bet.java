package fr.karmaowner.casino;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bet {
  private int left;
  
  public TaskCreator taskBet;
  
  private double total;
  
  private Casino c;
  
  public HashMap<Player, Double> bet;
  
  public HashMap<Player, Boolean> isBet;
  
  public static final double MIN = 100.0D;
  
  public Bet(Casino c, int timeLeft) {
    this.c = c;
    this.bet = new HashMap<>();
    this.isBet = new HashMap<>();
    this.taskBet = null;
    this.left = timeLeft;
  }
  
  public void TimeLeft() {
    String msg = MessageUtils.getMessageFromConfig("mise-or-take-random-value");
    msg = msg.replaceAll("&", "§");
    msg = msg.replaceAll("%left%", "" + this.left);
    this.c.NotificatePlayers(msg);
  }
  
  public boolean Ready() {
    for (Player p : this.c.listPlayers) {
      if (this.isBet.get(p) == null)
        return false; 
    } 
    return true;
  }
  
  public void startMise() {
    TimeLeft();
    this.taskBet = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (--Bet.this.left > 0) {
              String msg = MessageUtils.getMessageFromConfig("left-time");
              msg = msg.replaceAll("&", "§");
              msg = msg.replaceAll("%left%", "" + Bet.this.left);
              Bet.this.c.NotificatePlayers(msg);
            } else if (Bet.this.Ready()) {
              if (Bet.this.taskBet != null)
                Bet.this.taskBet.cancelTask(); 
            } else {
              for (Player p : Bet.this.c.listPlayers) {
                if (Bet.this.isBet.get(p) == null) {
                  Bet.this.bet.put(p, 100.0D);
                  PlayerData data = PlayerData.getPlayerData(p.getName());
                  data.setMoney(data.getMoney().subtract(BigDecimal.valueOf(100.0D)));
                  Bet.this.isBet.put(p, Boolean.TRUE);
                  MessageUtils.sendMessageFromConfig((CommandSender)p, "empty-money-taked");
                } 
              } 
            } 
          }
        },false, 0L, 60L);
  }
  
  public void endMise(Player p) {
    for (Map.Entry<Player, Double> s : this.bet.entrySet()) {
      if (((Player)s.getKey()).getName().equals(p.getName())) {
        PlayerData data = PlayerData.getPlayerData(((Player)s.getKey()).getName());
        data.setMoney(data.getMoney().subtract(BigDecimal.valueOf((Double) s.getValue())));
        return;
      } 
    } 
  }
  
  public double totalBet() {
    for (Iterator<Double> iterator = this.bet.values().iterator(); iterator.hasNext(); ) {
      double b = (Double) iterator.next();
      this.total += b;
    } 
    return this.total;
  }
  
  public static void addBet(Player p, double Mise) {
    Casino c = (PlayerData.getPlayerData(p.getName())).c;
    if (c != null) {
      if (c.isBetActivated) {
        if (c.s == Casino.State.STARTED) {
          if (c.b.isBet.get(p) == null) {
            if (Mise >= 100.0D && Mise <= 10000.0D) {
              c.b.bet.put(p, Mise);
              PlayerData data = PlayerData.getPlayerData(p.getName());
              data.setMoney(data.getMoney().subtract(BigDecimal.valueOf(Mise)));
              c.b.isBet.put(p, Boolean.TRUE);
              String msg = MessageUtils.getMessageFromConfig("mise");
              msg = msg.replaceAll("%mise%", "" + Mise);
              MessageUtils.sendMessage((CommandSender)p, msg);
            } else {
              MessageUtils.sendMessageFromConfig((CommandSender)p, "invalid-mise");
            } 
          } else {
            MessageUtils.sendMessageFromConfig((CommandSender)p, "already-mised");
          } 
        } else {
          MessageUtils.sendMessageFromConfig((CommandSender)p, "game-not-started");
        } 
      } else {
        MessageUtils.sendMessageFromConfig((CommandSender)p, "not-mise-system");
      } 
    } else {
      MessageUtils.sendMessageFromConfig((CommandSender)p, "not-game-playing");
    } 
  }
}
