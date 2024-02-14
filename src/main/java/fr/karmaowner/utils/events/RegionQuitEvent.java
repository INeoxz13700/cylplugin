package fr.karmaowner.utils.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionQuitEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private Set<ProtectedRegion> rg;
  
  private Player p;
  
  public RegionQuitEvent(Set<ProtectedRegion> set, Player p) {
    this.rg = set;
    this.p = p;
  }
  
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  public Set<ProtectedRegion> getRegions() {
    return this.rg;
  }
  
  public Player getPlayer() {
    return this.p;
  }
}
