package fr.karmaowner.companies.shop.events;

import fr.karmaowner.companies.shop.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OpenShopEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private Npc npc;
  
  private Player p;
  
  private boolean cancelled;
  
  public OpenShopEvent(Player p, Npc npc) {
    this.p = p;
    this.npc = npc;
  }
  
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  public Npc getNpc() {
    return this.npc;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean arg0) {
    this.cancelled = arg0;
  }
  
  public Player getPlayer() {
    return this.p;
  }
}
