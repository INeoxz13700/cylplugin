package fr.karmaowner.jobs.hacker;

import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.utils.MoneyUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AtmHacking extends SlitheGame {
  private enum SYMBOL {
    X(160, (byte)5, true),
    O(160, (byte)13, false),
    T(160, (byte)6, true),
    R(160, (byte)14, false);
    
    private int id;
    
    private byte data;
    
    private boolean clickable;
    
    SYMBOL(int id, byte data, boolean clickable) {
      this.id = id;
      this.data = data;
      this.clickable = clickable;
    }
    
    SYMBOL(int id, boolean clickable) {
      this.id = id;
      this.data = 0;
      this.clickable = clickable;
    }
    
    public boolean isClickable() {
      return this.clickable;
    }
    
    public int getId() {
      return this.id;
    }
    
    public byte getData() {
      return this.data;
    }
  }
  
  private static final SYMBOL[][] PLATFORM1 = new SYMBOL[][] { { SYMBOL.X, SYMBOL.O, SYMBOL.O, SYMBOL.X, SYMBOL.X, SYMBOL.O, SYMBOL.O, SYMBOL.X, SYMBOL.X }, { SYMBOL.O, SYMBOL.O, SYMBOL.X, SYMBOL.O, SYMBOL.O, SYMBOL.X, SYMBOL.X, SYMBOL.O, SYMBOL.O }, { SYMBOL.O, SYMBOL.X, SYMBOL.O, SYMBOL.O, SYMBOL.O, SYMBOL.O, SYMBOL.O, SYMBOL.O, SYMBOL.O } };
  
  private static final SYMBOL[][] PLATFORM2 = new SYMBOL[][] { { SYMBOL.R, SYMBOL.R, SYMBOL.T, SYMBOL.R, SYMBOL.T, SYMBOL.R, SYMBOL.T, SYMBOL.T, SYMBOL.R }, { SYMBOL.R, SYMBOL.T, SYMBOL.R, SYMBOL.R, SYMBOL.R, SYMBOL.T, SYMBOL.R, SYMBOL.R, SYMBOL.T }, { SYMBOL.T, SYMBOL.R, SYMBOL.R, SYMBOL.T, SYMBOL.R, SYMBOL.R, SYMBOL.R, SYMBOL.R, SYMBOL.R } };
  
  private ArrayList<SYMBOL[][]> platforms = new ArrayList<>();
  
  private SYMBOL[][] choosen;
  
  private ArrayList<SYMBOL> symbols = new ArrayList<>();
  
  public AtmHacking(int slots, Player p, Hacker h) {
    super(slots, ChatColor.DARK_PURPLE + "ATM Hacking", p, h);
    this.platforms.add(PLATFORM1);
    this.platforms.add(PLATFORM2);
    for (int i = 0; i < 9; i++) {
      if (i % 2 == 0) {
        moveColumnLeft(i + 1, 20L);
      } else {
        moveColumnRight(i + 1, 20L);
      } 
    } 
    SYMBOL[][] random = this.platforms.get((int)(Math.random() * this.platforms.size()));
    this.choosen = random;
    for (int j = 0; j < random.length; j++) {
      for (int k = 0; k < (random[j]).length; k++) {
        if (!isSymbol(random[j][k]))
          this.symbols.add(random[j][k]); 
        addItem(j * (random[j]).length + k, new ItemStack(random[j][k].getId(), 1, (short)0, random[j][k].getData()));
      } 
    } 
  }
  
  public boolean isSymbol(SYMBOL s1) {
    for (SYMBOL s2 : this.symbols) {
      if (s1 == s2)
        return true; 
    } 
    return false;
  }
  
  public SYMBOL getClickableSymbol() {
    for (SYMBOL s2 : this.symbols) {
      if (s2.isClickable())
        return s2; 
    } 
    return null;
  }
  
  public SYMBOL getSymbol() {
    for (SYMBOL s2 : this.symbols) {
      if (!s2.isClickable())
        return s2; 
    } 
    return null;
  }
  
  public boolean isWinner() {
    for (int i = 9; i < 18; i++) {
      boolean isItemOnRightCase = false;
      for (SYMBOL s : SYMBOL.values()) {
        if (s.isClickable()) {
          ItemStack item = getInventory().getItem(i);
          if (item.getTypeId() == s.getId() && item.getData().getData() == s.getData())
            isItemOnRightCase = true; 
        } 
      } 
      if (!isItemOnRightCase)
        return false; 
    } 
    return true;
  }
  
  public void resetRow(int row) {
    int start = row * 9;
    int lastCol = start + 9;
    for (int i = start; i < lastCol; i++)
      addItem(i, new ItemStack(getSymbol().getId(), 1, (short)0, getSymbol().getData()));
  }
  
  public void resetCol(int col) {
    int start = col;
    int last = col + 18;
    for (int i = start; i <= last; i += 9)
      addItem(i, new ItemStack(getSymbol().getId(), 1, (short)0, getSymbol().getData()));
  }
  
  public void winGame(ItemStack itemClicked, int slot) {
    if (isWinner()) {
      end();
      PlayerData data = PlayerData.getPlayerData(getPlayer().getName());
      Hacker h = (Hacker)data.selectedJob;
      double min = 500.0D;
      double max = 1000.0D;
      int gain = (int)(Math.random() * (max - min) + min);
      MoneyUtils.convertValueToTickets(gain, getPlayer());
      h.timer = new Timestamp(System.currentTimeMillis());
      getPlayer().sendMessage(ChatColor.GREEN + "Très beau travail ! Vous venez de recevoir dans votre inventaire " + ChatColor.DARK_GREEN + gain + "€");
      return;
    } 
    for (SYMBOL s : SYMBOL.values()) {
      if (s.isClickable() && 
        itemClicked.getTypeId() == s.getId() && itemClicked.getData().getData() == s.getData()) {
        int col = slot % 9;
        int middle = col + 9;
        if (slot == middle) {
          Movement m = MovementBuilder.build().setColumn().setPosition(col + 1).getMovement();
          TaskCreator t = getTasks(m);
          if (t != null) {
            t.cancelTask();
            getTasks().remove(m);
            resetCol(col);
            addItem(middle, new ItemStack(getClickableSymbol().getId(), 1, (short)0, getClickableSymbol().getData()));
            return;
          } 
        } 
      } 
    } 
    getPlayer().sendMessage(ChatColor.RED + "Vous avez échoué ! Réessayez plus tard !");
    end();
  }
  
  public String HackingType() {
    return "Piratage d'un ATM";
  }
}
