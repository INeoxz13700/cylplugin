package fr.karmaowner.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VehicleEvent extends Event {
  private static final HandlerList HANDLER = new HandlerList();
  
  private String vehicleName;
  
  private Player player;
  
  public VehicleEvent(String vehicleName, Player player) {
    this.vehicleName = vehicleName;
    this.player = player;
  }
  
  public HandlerList getHandlers() {
    return HANDLER;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLER;
  }
  
  public String getVehicleName() {
    return this.vehicleName;
  }
  
  public Player getPlayer() {
    return this.player;
  }
}
