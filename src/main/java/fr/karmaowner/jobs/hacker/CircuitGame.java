package fr.karmaowner.jobs.hacker;

import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import java.awt.Point;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class CircuitGame extends MakeHackingGame {
  public final Point TOPLEFT = new Point(0, 0);
  
  public final Point TOPRIGHT = new Point(8, 0);
  
  public final Point BOTTOMLEFT = new Point(0, 
      getSlots() / 9 - 1);
  
  public final Point BOTTOMRIGHT = new Point(this.TOPRIGHT.x, this.BOTTOMLEFT.y);
  
  public boolean[][] path;
  
  private ItemStack startPoint = ItemUtils.getItem(160, (byte)6, 1, "§cPoint de départ du chemin", null);
  
  private ItemStack endPoint = ItemUtils.getItem(160, (byte)14, 1, "§4Point d'arrivé du chemin", null);
  
  private ItemStack goodPath = ItemUtils.getItem(160, (byte)5, 1, "§aChemin du circuit", null);
  
  private ItemStack posPath = ItemUtils.getItem(160, (byte)3, 1, "§9Chemin parcouru", null);
  
  private ItemStack badPath = ItemUtils.getItem(160, (byte)13, 1, "§2Court-circuit", null);
  
  private Point from;
  
  private Point to;
  
  public abstract void winGame();
  
  public CircuitGame(int slots, Player p, Hacker h) {
    super(slots, "§5Décodeur de circuit", p, h);
    this.path = new boolean[9][slots / 9];
    definePath();
    fillInventory();
  }
  
  public void definePath() {
    Point top = ((int)(Math.random() * 2.0D) == 0) ? this.TOPLEFT : this.TOPRIGHT;
    Point bottom = (top == this.TOPLEFT) ? this.BOTTOMRIGHT : this.BOTTOMLEFT;
    this.from = (top == this.TOPLEFT) ? new Point(top.x, top.y) : new Point(this.BOTTOMLEFT.x, this.BOTTOMLEFT.y);
    this.to = (bottom == this.BOTTOMRIGHT) ? new Point(bottom.x, bottom.y) : new Point(this.TOPRIGHT.x, this.TOPRIGHT.y);
    int posX = this.from.x;
    int posY = this.from.y;
    this.path[posX][posY] = true;
    while (posX != this.to.x || posY != this.to.y) {
      boolean take = false;
      if (posX != this.to.x) {
        take = ((int)(Math.random() * 2.0D) == 1);
        if (this.to.x > posX && 
          take)
          posX++; 
        if (this.to.x < posX && 
          take)
          posX--; 
      } 
      if (posY != this.to.y) {
        take = !take;
        if (this.to.y > posY && 
          take)
          posY++; 
        if (this.to.y < posY && 
          take)
          posY--; 
      } 
      this.path[posX][posY] = true;
    } 
  }
  
  public int slot(int x, int y) {
    return x + y * 9;
  }
  
  public Point coord(int slot) {
    int y = (int)Math.ceil(slot / 9.0D);
    int x = slot - y * 9;
    return new Point(x, y);
  }
  
  public void fillInventory() {
    for (int i = 0; i < getSlots(); i++) {
      Point dot = coord(i);
      if (dot.equals(this.from)) {
        getInventory().setItem(i, this.startPoint);
      } else if (dot.equals(this.to)) {
        getInventory().setItem(i, this.endPoint);
      } else {
        int x = dot.x;
        int y = dot.y;
        ItemStack item = this.path[x][y] ? this.goodPath : this.badPath;
        getInventory().setItem(i, item);
      } 
    } 
  }
  
  public void winGame(ItemStack itemClicked, int slot) {
    Point currentClickedPos = coord(slot);
    if (itemClicked.getItemMeta().getDisplayName().equals(this.posPath.getItemMeta().getDisplayName()))
      return; 
    if (currentClickedPos.distance(this.from) > 1.0D || (getInventory().contains(this.startPoint) && 
      !itemClicked.getItemMeta().getDisplayName().equals(this.startPoint.getItemMeta().getDisplayName())))
      return; 
    int DestSlot = slot(this.to.x, this.to.y);
    if (!this.path[currentClickedPos.x][currentClickedPos.y]) {
      MessageUtils.sendMessage((CommandSender)getPlayer(), "§cVous avez échoué ! Réessayez plus tard !");
      end();
      return;
    } 
    this.from.x = currentClickedPos.x;
    this.from.y = currentClickedPos.y;
    getInventory().setItem(slot, this.posPath);
    if (slot == DestSlot) {
      MessageUtils.sendMessage((CommandSender)getPlayer(), "§aVous avez réussi à pirater le circuit !");
      winGame();
      end();
      return;
    } 
  }
}
