package fr.karmaowner.chat.events;

import fr.karmaowner.chat.ChatGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SendMessageEvent extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private boolean cancelled;
  
  private String msg;
  
  private Player p;
  
  private ChatGroup g;
  
  public SendMessageEvent(String msg, Player p, ChatGroup chatGroup) {
    this.msg = msg;
    this.p = p;
    this.g = chatGroup;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean arg0) {
    this.cancelled = arg0;
  }
  
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  public String getMessage() {
    return this.msg;
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public ChatGroup getChatGroup() {
    return this.g;
  }
}
