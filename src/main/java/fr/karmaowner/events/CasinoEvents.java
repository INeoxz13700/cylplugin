package fr.karmaowner.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.casino.Casino;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.events.RegionQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class CasinoEvents implements Listener {
  abstract void onInventoryClick(InventoryClickEvent paramInventoryClickEvent);
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    Casino c = Casino.getGame(p);
    if (c != null)
      if (c.s == Casino.State.STARTED) {
        if (c.nbPlayers == 1) {
          Casino.waitingGames.remove(c);
        } else if (c.nbPlayers > 1) {
          if (c.isBetActivated)
            c.b.endMise(p); 
          c.listPlayers.remove(p);
          c.NotificatePlayers("Un joueur a quitté la partie " + c.listPlayers.size() + "/" + c.nbPlayers);
        } 
        (PlayerData.getPlayerData(p.getName())).c = null;
      } else if (c.s == Casino.State.WAITING) {
        Casino.StopWaiting(p);
        (PlayerData.getPlayerData(p.getName())).c = null;
      }  
  }
  
  @EventHandler
  public void leaveCasino(RegionQuitEvent e) {
    Player p = e.getPlayer();
    for (ProtectedRegion r : e.getRegions()) {
      if (r.getId().equalsIgnoreCase("Casino")) {
        Casino c = Casino.getGame(p);
        if (c != null)
          if (c.s == Casino.State.STARTED) {
            if (c.nbPlayers == 1) {
              Casino.waitingGames.remove(c);
            } else if (c.nbPlayers > 1) {
              if (c.isBetActivated)
                c.b.endMise(p); 
              c.listPlayers.remove(p);
              c.NotificatePlayers("Un joueur a quitté la partie " + c.listPlayers.size() + "/" + c.nbPlayers);
            } 
            (PlayerData.getPlayerData(p.getName())).c = null;
          } else if (c.s == Casino.State.WAITING) {
            Casino.StopWaiting(p);
            (PlayerData.getPlayerData(p.getName())).c = null;
          }  
        return;
      } 
    } 
  }
}
