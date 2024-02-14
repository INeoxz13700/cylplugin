package fr.karmaowner.casino;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import java.math.BigDecimal;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.FireworkMeta;

public class RouletteGame extends Casino {
  public Player p;
  
  public HashMap<Player, Double> multiply;
  
  private HashMap<Player, Double> mise;
  
  private TaskCreator ChoosingMultiply;
  
  private int TimeMultiply;
  
  private int TimeLeft;
  
  private int Time;
  
  public final double[] itemRatio = new double[] { 1.5D, 2.0D, 2.5D };
  
  private final int[] idMaterial = new int[] { 14, 3, 4 };
  
  private final Color[] FIREWORKCOLOR = new Color[] { Color.RED, Color.BLUE, Color.YELLOW };
  
  public RouletteGame(Player p) {
    super(10, 5, false, Casino.GameType.Roulette.getBetStatut());
    this.b = new Bet(this, 10);
    this.p = p;
    this.multiply = new HashMap<>();
    this.TimeMultiply = 5;
    this.TimeLeft = (int)(Math.random() * 3.0D + 30.0D);
    this.Time = this.TimeLeft;
    this.inv = Bukkit.createInventory(null, 27, ChatColor.BOLD + "" + ChatColor.BLUE + "Roulette");
    addItemInventory(Material.STAINED_GLASS_PANE, 14, 11, ChatColor.RED + "x1.5");
    addItemInventory(Material.STAINED_GLASS_PANE, 3, 13, ChatColor.BLUE + "x2");
    addItemInventory(Material.STAINED_GLASS_PANE, 4, 15, ChatColor.YELLOW + "x2.5");
  }
  
  public void WaitPlayerMultiply() {
    this.ChoosingMultiply = new TaskCreator(new CustomRunnable() {
          public void customRun() {
            if (!RouletteGame.this.b.taskBet.getRunningStatus()) {
              RouletteGame.this.openInventory();
              if (--RouletteGame.this.TimeMultiply <= 0) {
                RouletteGame.this.closeInventory();
                for (Player p : RouletteGame.this.listPlayers) {
                  if (RouletteGame.this.multiply.get(p) == null) {
                    int random = (int)(Math.random() * RouletteGame.this.itemRatio.length);
                    RouletteGame.this.multiply.put(p, RouletteGame.this.itemRatio[random]);
                    p.sendMessage("§cNous avons choisi à votre place le multiplicateur par x" + RouletteGame.this.multiply.get(p));
                  } 
                } 
                RouletteGame.this.ChoosingMultiply.cancelTask();
                RouletteGame.this.loadGame();
              } else {
                RouletteGame.this.NotificatePlayers("§6Temps restant avant le début du tirage: §e" + RouletteGame.this.TimeMultiply);
              } 
            } 
          }
        }, false, 0L, 40L);
  }
  
  public void Winner(Byte id) {
    int i;
    for (i = 0; i < this.idMaterial.length && 
      this.idMaterial[i] != id.byteValue(); i++);
    for (Player p : this.listPlayers) {
      if ((Double) this.multiply.get(p) == this.itemRatio[i]) {
        final Firework f = (Firework)p.getWorld().spawn(p.getLocation(), Firework.class);
        FireworkMeta effect = f.getFireworkMeta();
        effect.setPower(1);
        effect.addEffect(FireworkEffect.builder()
            .withColor(this.FIREWORKCOLOR[i])
            .flicker(true)
            .build());
        f.setFireworkMeta(effect);
        new TaskCreator(new CustomRunnable() {
              public void customRun() {
                f.detonate();
                cancel();
              }
            },  false, 10L, 0L);
        double total = (Double) this.b.bet.get(p) * (Double) this.multiply.get(p);
        p.sendMessage("§aVous venez de remporter la cagnotte");
        p.sendMessage("§aVotre cagnotte s'élève à " + total);
        PlayerData data = PlayerData.getPlayerData(p.getName());
        data.setMoney(data.getMoney().add(BigDecimal.valueOf(total)));
        continue;
      } 
      p.sendMessage("§cVous avez perdu l'intégralité de votre mise");
    } 
  }
  
  public void loadGame() {
    this.inv = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.BOLD + "" + ChatColor.BLUE + "Roulette");
    for (int i = 0; i < 5; i++)
      addItemInventory(Material.STAINED_GLASS_PANE, this.idMaterial[i % 3], i, ""); 
    setTaskGame(new TaskCreator(new CustomRunnable() {
            public void customRun() {
              RouletteGame.this.openInventory();
              if (--RouletteGame.this.TimeLeft <= 0) {
                byte id = RouletteGame.this.inv.getItem(2).getData().getData();
                RouletteGame.this.Winner(id);
                RouletteGame.this.getTaskGame().cancelTask();
                RouletteGame.this.NotificatePlayers("§cLa couleur qui remporte la partie est le §a" + ((id == 14) ? (ChatColor.RED + "Rouge") : ((id == 3) ? (ChatColor.BLUE + "Bleu") : (ChatColor.YELLOW + "Jaune"))));
                RouletteGame.this.endGame();
              } else {
                for (int i = 0; i < 5; i++)
                  RouletteGame.this.addItemInventory(Material.STAINED_GLASS_PANE, RouletteGame.this.idMaterial[(RouletteGame.this.Time - RouletteGame.this.TimeLeft + i) % 3], i, ""); 
              } 
            }
          }, false, 0L, 20L));
  }
  
  public void startGame() {
    if (this.s == Casino.State.WAITING) {
      this.s = Casino.State.STARTED;
      this.b.startMise();
      this.b.taskBet.WhenTaskFinished(new CustomRunnable() {
            public void customRun() {
              RouletteGame.this.WaitPlayerMultiply();
            }
          });
    } 
  }
  
  public String toString() {
    return ChatColor.GREEN + "Roulette" + super.toString();
  }
}
