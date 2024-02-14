package fr.karmaowner.tresorerie;

import org.bukkit.entity.Player;

public class StaffTresorerie extends Tresorerie implements TresoreriePickable {
  public StaffTresorerie() {
    super("staff");
  }
  
  public boolean hasPrivilege(Player p) {
    return p.hasPermission("cylrp.tresorerie.staff");
  }
  
  public String getNeededPrivilege() {
    return "Vous devez être membre du staff";
  }
}
