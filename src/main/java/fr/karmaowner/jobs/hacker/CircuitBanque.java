package fr.karmaowner.jobs.hacker;

import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CircuitBanque extends CircuitGame {
  private ItemStack CardLevel;
  
  public CircuitBanque(int slots, Player p, Hacker h, ItemStack CardLevel) {
    super(slots, p, h);
    this.CardLevel = CardLevel;
  }
  
  public void winGame() {
    if (this.CardLevel != null)
      ItemUtils.addItem(this.CardLevel, getPlayer()); 
  }
  
  public String HackingType() {
    return "Tentative de piratage de la porte d'entrée de la banque";
  }
}
