package fr.karmaowner.casino;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class TictactocGame extends Casino {
  public BitSet casesT1;
  
  public BitSet casesT2;
  
  public Player p1;
  
  public Player p2;
  
  public int tempsRestant;
  
  public int temps;
  
  public TEAM t1 = TEAM.ROUND;
  
  public TEAM t2 = TEAM.CROSS;
  
  public Player tour;
  
  public int[] physicCase = new int[] { 
      0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 
      0, 0, 3, 4, 5, 0, 0, 0, 0, 0, 
      0, 6, 7, 8, 0, 0, 0 };
  
  public enum TEAM {
    CROSS(Material.BLAZE_ROD, "Croix"),
    ROUND(Material.SLIME_BALL, "Rond");
    
    private String string;
    
    private Material id;
    
    TEAM(Material id, String string) {
      this.id = id;
      this.string = string;
    }
    
    public Material getId() {
      return this.id;
    }
    
    public String getString() {
      return this.string;
    }
  }
  
  public TictactocGame(Player p) {
    super(2, 10, true, Casino.GameType.Tictactoc.getBetStatut());
    this.tour = p;
    this.casesT1 = new BitSet(9);
    this.casesT2 = new BitSet(9);
    this.b = new Bet(this, 10);
    this.p1 = p;
    this.tempsRestant = 10;
    this.temps = 10;
    this.inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Tictactoc");
    fillInventory(0, "");
  }
  
  public void startGame() {
    if (this.s.equals(Casino.State.WAITING)) {
      this.p2 = this.listPlayers.get(1);
      this.s = Casino.State.STARTED;
      this.b.startMise();
      this.b.taskBet.WhenTaskFinished(new CustomRunnable() {
            public void customRun() {
              TictactocGame.this.fillInventory(11, ChatColor.DARK_BLUE + TictactocGame.this.p1.getName());
              TictactocGame.this.GameFlow();
            }
          });
    } 
  }
  
  public void fillInventory(int id, String name) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j <= 8; j++) {
        int index = i * 9 + j;
        if ((index < 3 || index > 5) && (index < 12 || index > 14) && (index < 21 || index > 23))
          addItemInventory(Material.STAINED_GLASS_PANE, id, index, name); 
      } 
    } 
  }
  
  public boolean checkIfEndGame() {
    for (int i = 0; i < this.inv.getSize(); i++) {
      if (this.inv.getItem(i) == null)
        return false; 
    } 
    return true;
  }
  
  public void GameFlow() {
    setTaskGame(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              TictactocGame.this.openInventory();
              if (TictactocGame.this.listPlayers.size() == 1)
                TictactocGame.this.endGame(TictactocGame.this.listPlayers.get(0)); 
              if (TictactocGame.this.checkIfEndGame())
                TictactocGame.this.endGame((Player)null); 
              if (--TictactocGame.this.tempsRestant <= 0) {
                for (int i = 0; i < (TictactocGame.this.inv.getContents()).length; i++) {
                  if (TictactocGame.this.inv.getContents()[i] == null) {
                    TictactocGame.this.addItemInventory(TictactocGame.this.tour.equals(TictactocGame.this.p1) ? TictactocGame.this.t1.getId() : TictactocGame.this.t2.getId(), 1, i, 
                        TictactocGame.this.tour.equals(TictactocGame.this.p1) ? TictactocGame.this.t1.getString() : TictactocGame.this.t2.getString());
                    if (TictactocGame.this.tour.equals(TictactocGame.this.p1)) {
                      TictactocGame.this.casesT1.set(TictactocGame.this.physicCase[i]);
                      TictactocGame.this.tour = TictactocGame.this.p2;
                    } else {
                      TictactocGame.this.casesT2.set(TictactocGame.this.physicCase[i]);
                      TictactocGame.this.tour = TictactocGame.this.p1;
                    } 
                    if (TictactocGame.this.checkIfWinner() != null)
                      TictactocGame.this.endGame(TictactocGame.this.checkIfWinner()); 
                    break;
                  } 
                } 
                TictactocGame.this.tempsRestant = TictactocGame.this.temps;
                TictactocGame.this.tour.sendMessage("§aC'est à votre tour !");
              } 
              if (TictactocGame.this.tour.equals(TictactocGame.this.p1)) {
                TictactocGame.this.fillInventory(11, ChatColor.DARK_BLUE + TictactocGame.this.p1.getName());
              } else {
                TictactocGame.this.fillInventory(14, ChatColor.DARK_RED + TictactocGame.this.p2.getName());
              } 
            }
          }, false, 0L, 20L));
  }
  
  public void endGame(Player player) {
    if (player != null) {
      getTaskGame().cancelTask();
      final Firework f = (Firework)player.getWorld().spawn(player.getLocation(), Firework.class);
      FireworkMeta effect = f.getFireworkMeta();
      effect.setPower(1);
      effect.addEffect(FireworkEffect.builder()
          .withColor(this.p1.equals(player) ? Color.BLUE : Color.RED)
          .flicker(true)
          .build());
      f.setFireworkMeta(effect);
      new TaskCreator(new CustomRunnable() {
            public void customRun() {
              f.detonate();
              cancel();
            }
          },  false, 10L, 0L);
      double miseTotale = this.b.totalBet();
      NotificatePlayers("§6Partie terminée: Le gagnant est §e" + player.getName());
      player.sendMessage("§6Vous venez de remporter §e" + miseTotale + "€");
      PlayerData data = PlayerData.getPlayerData(player.getName());
      data.setMoney(data.getMoney().add(BigDecimal.valueOf(miseTotale)));
    } else {
      getTaskGame().cancelTask();
      for (Map.Entry<Player, Double> rendre : this.b.bet.entrySet()) {
        PlayerData data = PlayerData.getPlayerData(((Player)rendre.getKey()).getName());
        data.setMoney(data.getMoney().add(BigDecimal.valueOf((Double) rendre.getValue())));
      } 
      NotificatePlayers("§ePartie terminée: aucun gagnant");
    } 
    endGame();
  }
  
  public Player checkIfWinner() {
    boolean pos1T1 = (this.casesT1.get(0) && this.casesT1.get(1) && this.casesT1.get(2));
    boolean pos2T1 = (this.casesT1.get(3) && this.casesT1.get(4) && this.casesT1.get(5));
    boolean pos3T1 = (this.casesT1.get(6) && this.casesT1.get(7) && this.casesT1.get(8));
    boolean pos4T1 = (this.casesT1.get(0) && this.casesT1.get(4) && this.casesT1.get(8));
    boolean pos5T1 = (this.casesT1.get(6) && this.casesT1.get(4) && this.casesT1.get(2));
    boolean pos6T1 = (this.casesT1.get(0) && this.casesT1.get(3) && this.casesT1.get(6));
    boolean pos7T1 = (this.casesT1.get(1) && this.casesT1.get(4) && this.casesT1.get(7));
    boolean pos8T1 = (this.casesT1.get(2) && this.casesT1.get(5) && this.casesT1.get(8));
    boolean pos1T2 = (this.casesT2.get(0) && this.casesT2.get(1) && this.casesT2.get(2));
    boolean pos2T2 = (this.casesT2.get(3) && this.casesT2.get(4) && this.casesT2.get(5));
    boolean pos3T2 = (this.casesT2.get(6) && this.casesT2.get(7) && this.casesT2.get(8));
    boolean pos4T2 = (this.casesT2.get(0) && this.casesT2.get(4) && this.casesT2.get(8));
    boolean pos5T2 = (this.casesT2.get(6) && this.casesT2.get(4) && this.casesT2.get(2));
    boolean pos6T2 = (this.casesT2.get(0) && this.casesT2.get(3) && this.casesT2.get(6));
    boolean pos7T2 = (this.casesT2.get(1) && this.casesT2.get(4) && this.casesT2.get(7));
    boolean pos8T2 = (this.casesT2.get(2) && this.casesT2.get(5) && this.casesT2.get(8));
    if (pos1T1 || pos2T1 || pos3T1 || pos4T1 || pos5T1 || pos6T1 || pos7T1 || pos8T1)
      return this.p1; 
    if (pos1T2 || pos2T2 || pos3T2 || pos4T2 || pos5T2 || pos6T2 || pos7T2 || pos8T2)
      return this.p2; 
    return null;
  }
  
  public String toString() {
    return ChatColor.DARK_AQUA + "Tictactoc" + super.toString();
  }
}
