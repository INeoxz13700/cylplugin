package fr.karmaowner.jobs.chauffeur;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Bus {
  public static final int delaySeconds = 300;
  
  public static final double price = 50.0D;
  
  private TaskCreator task;
  
  private Player p;
  
  private ProtectedRegion shortPath = null;
  
  private boolean otherside = false;
  
  private HashMap<ProtectedRegion, Boolean> usedRegions = new HashMap<>();
  
  public Bus(Player p) {
    this.p = p;
    for (Regions r : Regions.listRegions) {
      if (r.getRg() != null)
        this.usedRegions.put(r.getRg(), Boolean.FALSE);
    } 
  }
  
  public ProtectedRegion getShortPath() {
    if (this.shortPath != null)
      return this.shortPath; 
    ProtectedRegion shortRg = null;
    if (!hasAvailableRegion()) {
      this.otherside = !this.otherside;
      reset();
    } 
    if (!this.otherside) {
      for (int i = Regions.listRegions.size() - 1; i >= 0; i--) {
        Regions r = Regions.listRegions.get(i);
        boolean alreadyUsed = (Boolean) this.usedRegions.get(r.getRg());
        if (!alreadyUsed)
          shortRg = r.getRg(); 
      } 
    } else {
      for (int i = 0; i < Regions.listRegions.size(); i++) {
        Regions r = Regions.listRegions.get(i);
        boolean alreadyUsed = this.usedRegions.get(r.getRg()).booleanValue();
        if (!alreadyUsed)
          shortRg = r.getRg(); 
      } 
    } 
    this.shortPath = shortRg;
    return this.shortPath;
  }
  
  public void addValue(ProtectedRegion rg) {
    if (rg != null)
      this.usedRegions.put(rg, Boolean.TRUE);
  }
  
  public Location getRgLocation(ProtectedRegion rg) {
    return (rg == null) ? null : new Location(this.p.getWorld(), rg.getMinimumPoint().getBlockX(), rg
        .getMinimumPoint().getBlockY(), rg.getMinimumPoint().getBlockZ());
  }
  
  public void reset() {
    for (Map.Entry<ProtectedRegion, Boolean> r : this.usedRegions.entrySet())
      this.usedRegions.put(r.getKey(), Boolean.FALSE);
  }
  
  public boolean hasAvailableRegion() {
    for (Map.Entry<ProtectedRegion, Boolean> r : this.usedRegions.entrySet()) {
      if (r.getKey() != null && !(Boolean) r.getValue())
        return true; 
    } 
    return false;
  }
  
  public boolean startService() {
    if (this.task != null)
      return false; 
    this.task = new TaskCreator(new CustomRunnable() {
          private long messageElapsed = 0L;
          
          public void customRun() {
            ProtectedRegion rg = Bus.this.getShortPath();
            if (!Bus.this.p.isOnline()) {
              Bus.this.stopService();
              return;
            }

            PlayerData data = PlayerData.getPlayerData(Bus.this.p.getName());
            if (Bukkit.getPlayerExact(Bus.this.p.getName()) == null || !(data.selectedJob instanceof fr.karmaowner.jobs.Taxi)) {
              Bus.this.stopService();
              return;
            } 
            if (rg == null) {
              MessageUtils.sendMessage((CommandSender)Bus.this.p, "Vous avez fait le tour de toutes les régions. Service arrêté");
              Bus.this.stopService();
              return;
            } 
            Location rgLoc = Bus.this.getRgLocation(Bus.this.shortPath);
            PlayerUtils utils = new PlayerUtils();
            utils.setPlayer(Bus.this.p);
            ArrayList<Player> players = utils.getNearbyPlayers(10.0D);
            if (System.currentTimeMillis() - this.messageElapsed >= 10000L) {
              this.messageElapsed = System.currentTimeMillis();
              for (Player pl : players)
                MessageUtils.sendMessage((CommandSender)pl, "§6Bus en service autour de vous"); 
            } 
            if (rgLoc != null) {
              Bus.this.p.setCompassTarget(rgLoc);
              Location ploc = Bus.this.p.getLocation();
              ploc.setY(rgLoc.getY());
              rgLoc.setY(rgLoc.getY());
              Vector d = ploc.getDirection();
              Vector v = Bus.this.p.getCompassTarget().subtract(ploc).toVector().normalize();
              double a = Math.toDegrees(Math.atan2(d.getX(), d.getZ()));
              a -= Math.toDegrees(Math.atan2(v.getX(), v.getZ()));
              a = ((int)(a + 22.5D) % 360);
              if (a < 0.0D)
                a += 360.0D; 
              int distance = (int)Bus.this.p.getLocation().distance(rgLoc);
              if (data.ActuallyRegion != null && data.ActuallyRegion.getId().equals(Bus.this.shortPath.getId())) {
                if (Bus.this.p != null) {
                  MessageUtils.sendMessage((CommandSender)Bus.this.p, "§aVous êtes arrivé à destination");
                  data.setMoney(data.getMoney().add(BigDecimal.valueOf(50.0D)));
                  MessageUtils.sendMessage((CommandSender)Bus.this.p, "§aRécompense: §250.0€");
                  Bus.this.addValue(Bus.this.shortPath);
                  Bus.this.shortPath = null;
                } 
              } else {
                Bus.this.p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6GPS §e: §o" + "⬆↗➡↘⬇↙⬅↖".charAt((int)a / 45) + " §e(§7" + distance + "m§e)"));
              }
            } 
          }
        }, false, 0L, 20L);
    return true;
  }
  
  public boolean stopService() {
    if (this.task != null) {
      this.task.cancelTask();
      this.task = null;
      return true;
    } 
    return false;
  }
}
