package fr.karmaowner.jobs.voleur;

import org.bukkit.entity.Player;

public class Caisse extends Braquage {
  public static final String INVENTORYNAME = "§eCaisse Enregistreuse";
  
  public Caisse(Player p) {
    super("§eCaisse Enregistreuse", "caisse", p);
  }
  
  public void saveData() {}
}
