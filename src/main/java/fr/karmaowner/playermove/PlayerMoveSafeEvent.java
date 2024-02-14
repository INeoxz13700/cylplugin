package fr.karmaowner.playermove;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMoveSafeEvent extends Event implements Cancellable {
  private static final HandlerList HANDLER = new HandlerList();
  
  private Location from;
  
  private Location to;
  
  private Player p;
  
  private boolean cancelled;
  
  public PlayerMoveSafeEvent(Location from, Location to, Player p) {
    this.from = from;
    this.to = to;
    this.p = p;
  }
  
  public HandlerList getHandlers() {
    return HANDLER;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLER;
  }
  
  public Location getFrom() {
    return this.from;
  }
  
  public Location getTo() {
    return this.to;
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public void setCancelled(boolean arg0) {
    this.cancelled = arg0;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
}
