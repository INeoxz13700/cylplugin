package fr.karmaowner.utils.events;

import fr.karmaowner.jobs.RebelleTerroriste;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AttentatAreaStayEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private RebelleTerroriste.AttentatArea area;
  
  private Player p;
  
  public AttentatAreaStayEvent(RebelleTerroriste.AttentatArea area, Player p) {
    this.area = area;
    this.p = p;
  }
  
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  public RebelleTerroriste.AttentatArea getArea() {
    return this.area;
  }
  
  public Player getPlayer() {
    return this.p;
  }
}
