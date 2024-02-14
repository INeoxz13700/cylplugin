package fr.karmaowner.playermove;

import fr.karmaowner.common.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    if (e.getPlayer().getTicksLived() % 10 == 0) {
      PlayerMoveSafeEvent event = new PlayerMoveSafeEvent(e.getFrom(), e.getTo(), e.getPlayer());
      Main.INSTANCE.getServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        e.setCancelled(true);
        return;
      } 
    } 
  }
}
