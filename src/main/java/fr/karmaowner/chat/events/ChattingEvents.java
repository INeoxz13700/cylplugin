package fr.karmaowner.chat.events;

import fr.karmaowner.chat.Chat;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.common.Main;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.events.ConnectionEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChattingEvents implements Listener {
  @EventHandler(priority = EventPriority.LOWEST)
  public void onSendMessageNotAuthentified(PlayerChatEvent e) {
    if(!PlayerData.getPlayerData(e.getPlayer().getName()).passwordReceived)
    {
      e.setCancelled(true);
      return;
    }

    if (ConnectionEvents.PlayersAuth.get(e.getPlayer().getName()) != null && !((ConnectionEvents.oauth)ConnectionEvents.PlayersAuth.get(e.getPlayer().getName())).auth)
      e.setCancelled(true);


  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onSendMessage(PlayerChatEvent e) {
    if (e.getPlayer().hasPermission("staffchat") && e.getMessage().startsWith("*")) {
      e.setCancelled(true);
      return;
    } 
    Player p = e.getPlayer();
    ChatGroup group = Chat.getPlayerChatGroup(p.getName());
    if (group == null) {
      Chat.getDefaultGroup().addPlayer(p.getName());
      group = Chat.getPlayerChatGroup(p.getName());
    } 
    e.setCancelled(true);
    SendMessageEvent msgEvent = new SendMessageEvent(e.getMessage(), e.getPlayer(), group);
    Main.INSTANCE.getServer().getPluginManager().callEvent(msgEvent);
    if (!msgEvent.isCancelled()) {
      for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
        if (pl.hasPermission("cylrp.chat.see") && Chat.getPlayerChatGroup(pl.getName()) != null && group != null && 
          !Chat.getPlayerChatGroup(pl.getName()).getName().equals(group.getName()))
          pl.sendMessage("§9[CHAT-" + group.getName() + "] " + p.getName() + " : " + e.getMessage()); 
      } 
      if (group != null)
        group.sendMessage(p, e.getMessage()); 
    } 
  }
}
