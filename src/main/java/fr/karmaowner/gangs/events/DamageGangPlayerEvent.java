package fr.karmaowner.gangs.events;

import fr.karmaowner.data.GangData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageGangPlayerEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private Player damager;
  
  private Player victim;
  
  private GangData data;
  
  private EntityDamageByEntityEvent e;
  
  public DamageGangPlayerEvent(Player damager, Player victim, GangData data, EntityDamageByEntityEvent e) {
    this.damager = damager;
    this.victim = victim;
    this.data = data;
    this.e = e;
  }
  
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  public Player getDamager() {
    return this.damager;
  }
  
  public Player getVictim() {
    return this.victim;
  }
  
  public GangData getGangDataVictim() {
    return this.data;
  }
  
  public boolean isCancelled() {
    return this.e.isCancelled();
  }
  
  public void setDamage(double arg) {
    this.e.setDamage(arg);
  }
  
  public void setCancelled(boolean cancel) {
    this.e.setCancelled(cancel);
  }
}
