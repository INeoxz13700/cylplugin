package fr.karmaowner.chat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatFormatEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private boolean cancelled;
  
  private Player p;
  
  private String msgFormat;
  
  private String newMsgFormat;
  
  private String msg;
  
  public ChatFormatEvent(Player p, String msgFormat, String msg) {
    this.p = p;
    this.msg = msg;
    this.msgFormat = msgFormat;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean arg0) {
    this.cancelled = arg0;
  }
  
  public void setNewMsgFormat(String msgFormat) {
    this.newMsgFormat = msgFormat;
  }
  
  public String getNewMsgFormat() {
    return this.newMsgFormat;
  }
  
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public String getMsgFormat() {
    return this.msgFormat;
  }
  
  public String getMessage() {
    return this.msg;
  }
}
