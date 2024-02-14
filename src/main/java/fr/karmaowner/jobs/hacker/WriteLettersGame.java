package fr.karmaowner.jobs.hacker;

import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class WriteLettersGame extends MakeHackingGame {
  public final int[] A = new int[] { 
      3, 4, 5, 12, 14, 21, 22, 23, 30, 32, 
      39, 41, 48, 50 };
  
  public final int[] B = new int[] { 
      3, 4, 5, 12, 14, 21, 22, 23, 30, 31, 
      39, 41, 48, 49, 50 };
  
  public final int[] C = new int[] { 3, 4, 5, 12, 21, 30, 39, 48, 49, 50 };
  
  public final int[] D = new int[] { 
      3, 4, 12, 14, 21, 23, 30, 32, 39, 41, 
      48, 49 };
  
  public final int[] E = new int[] { 
      3, 4, 5, 12, 21, 22, 23, 30, 39, 48, 
      49, 50 };
  
  public final int[] F = new int[] { 3, 4, 5, 12, 21, 22, 23, 30, 39, 48 };
  
  public final int[] G = new int[] { 
      3, 4, 5, 12, 11, 21, 30, 31, 32, 39, 
      41, 48, 49, 50 };
  
  public final int[] H = new int[] { 
      3, 5, 12, 14, 21, 22, 23, 30, 32, 39, 
      41, 48, 50 };
  
  public final int[] I = new int[] { 3, 4, 5, 13, 22, 31, 40, 48, 49, 50 };
  
  public final int[] J = new int[] { 3, 4, 5, 13, 22, 31, 40, 48, 49 };
  
  public final int[] K = new int[] { 
      3, 5, 12, 14, 21, 22, 30, 32, 39, 41, 
      48, 50 };
  
  public final int[] M = new int[] { 
      3, 5, 12, 13, 14, 21, 22, 23, 30, 32, 
      39, 41, 48, 50 };
  
  public final int[] N = new int[] { 
      3, 5, 12, 14, 21, 23, 30, 31, 32, 39, 
      40, 41, 48, 50 };
  
  public final int[] O = new int[] { 
      3, 4, 5, 12, 14, 21, 23, 30, 32, 39, 
      41, 48, 49, 50 };
  
  public final int[] P = new int[] { 3, 4, 12, 14, 21, 23, 30, 31, 39, 48 };
  
  private ItemStack item = ItemUtils.getItem(160, (byte)9, 1, "§3Clique-ici pour compléter la lettre", null);
  
  private ItemStack clicked = ItemUtils.getItem(160, (byte)11, 1, "§3Complété", null);
  
  private int[] letter;
  
  public abstract void winGame();
  
  public WriteLettersGame(char AtoPLetter, String nameInventory, Player p, Hacker h) {
    super(54, nameInventory, p, h);
    setLetter(AtoPLetter);
    fillInventory();
  }
  
  public void fillInventory() {
    getInventory().clear();
    for (int i : this.letter)
      getInventory().setItem(i, this.item); 
  }
  
  public boolean won() {
    for (int i : this.letter) {
      if (getInventory().getItem(i).getItemMeta()
        .getDisplayName().equals(this.item.getItemMeta().getDisplayName()))
        return false; 
    } 
    return true;
  }
  
  public boolean equaltoLetterSlot(int slot) {
    for (int i : this.letter) {
      if (i == slot)
        return true; 
    } 
    return false;
  }
  
  public void setLetter(char lt) {
    switch (Character.toLowerCase(lt)) {
      case 'a':
        this.letter = this.A;
        break;
      case 'b':
        this.letter = this.B;
        break;
      case 'c':
        this.letter = this.C;
        break;
      case 'd':
        this.letter = this.D;
        break;
      case 'e':
        this.letter = this.E;
        break;
      case 'f':
        this.letter = this.F;
        break;
      case 'g':
        this.letter = this.G;
        break;
      case 'h':
        this.letter = this.H;
        break;
      case 'i':
        this.letter = this.I;
        break;
      case 'j':
        this.letter = this.J;
        break;
      case 'k':
        this.letter = this.K;
        break;
      case 'm':
        this.letter = this.M;
        break;
      case 'n':
        this.letter = this.N;
        break;
      case 'o':
        this.letter = this.O;
        break;
      case 'p':
        this.letter = this.P;
        break;
    } 
  }
  
  public void winGame(ItemStack itemClicked, int slot) {
    if (equaltoLetterSlot(slot) && getInventory().getItem(slot)
      .getItemMeta().getDisplayName().equals(this.item.getItemMeta().getDisplayName()))
      getInventory().setItem(slot, this.clicked); 
    if (won()) {
      winGame();
      return;
    } 
  }
}
