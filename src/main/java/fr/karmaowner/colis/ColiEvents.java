package fr.karmaowner.colis;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

public class ColiEvents implements Listener {
  public static final int NUMBERELTS = 8;
  
  @EventHandler
  public void onColiThrow(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && 
      player.getItemInHand() != null && player.getItemInHand().hasItemMeta() && 
      player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(Coli.COLIDISPLAYNAME))
      (PlayerData.getPlayerData(player.getName())).launchedColi = true; 
  }
  
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    Projectile projectile = event.getEntity();
    if (projectile instanceof Snowball) {
      final Snowball snowball = (Snowball)projectile;
      Location loc = projectile.getLocation();
      Vector vec = projectile.getVelocity();
      final Location loc2 = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ());
      if (snowball.getShooter() instanceof Player) {
        Player player = (Player)snowball.getShooter();
        PlayerData data = PlayerData.getPlayerData(player.getName());
        if (data.launchedColi) {
          data.launchedColi = false;
          player.sendMessage(ChatColor.GREEN + "Vous venez de poser un coli ! En attente du coli...");
          player.sendMessage(ChatColor.DARK_GREEN + "Le coli arrive dans 15 secondes !");
          new TaskCreator(new CustomRunnable() {
                public void customRun() {
                  Coli.spawnColi(loc2, (Player)snowball.getShooter());
                  cancel();
                }
              }, false, 300L);
        } 
      } 
    } 
  }
  
  @EventHandler
  public void itemDropped(EntityChangeBlockEvent e) {
    if (e.getEntity() instanceof FallingBlock) {
      FallingBlock fb = (FallingBlock)e.getEntity();
      fb.setDropItem(false);
      if (fb.getMaterial() == Material.CHEST && fb
        .hasMetadata("coli")) {
        e.setCancelled(true);
        e.getBlock().setType(Material.CHEST);
        Chest c = (Chest)e.getBlock().getState();
        Inventory inv = c.getInventory();
        inv.addItem(Coli.getRandomItems(8));
        Coli.depopColi(c.getBlock());
      } 
    } 
  }
}
