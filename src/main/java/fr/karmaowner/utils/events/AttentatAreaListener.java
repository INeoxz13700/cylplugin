package fr.karmaowner.utils.events;

import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.RebelleTerroriste;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AttentatAreaListener implements Listener {
  private static boolean isReady = true;
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Location from = e.getFrom();
    Location to = e.getTo();
    Player p = e.getPlayer();
    if (isReady && 
      from.getBlockX() != to.getBlockX() && from.getBlockZ() != to.getBlockZ() && 
      RebelleTerroriste.AttentatArea.isInArea(p))
      Main.INSTANCE.getServer().getPluginManager().callEvent(new AttentatAreaStayEvent(RebelleTerroriste.AttentatArea.getArea(p), p)); 
  }
}
