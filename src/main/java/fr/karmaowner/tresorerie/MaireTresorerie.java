package fr.karmaowner.tresorerie;

import fr.karmaowner.data.PlayerData;
import org.bukkit.entity.Player;

public class MaireTresorerie extends Tresorerie {
  public MaireTresorerie() {
    super("maire");
  }
  
  public boolean hasPrivilege(Player p) {
    PlayerData data = PlayerData.getPlayerData(p.getName());
    return data.selectedJob instanceof fr.karmaowner.jobs.Maire;
  }
  
  public String getNeededPrivilege() {
    return "Vous n'êtes pas le Maire";
  }
}
