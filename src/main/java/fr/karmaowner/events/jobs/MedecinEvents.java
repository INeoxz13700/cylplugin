package fr.karmaowner.events.jobs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.cylapi.events.PlayerDeathAloneEnterReanimationEvent;
import fr.cylapi.events.PlayerEnterReanimationEvent;
import fr.cylapi.events.PlayerKilledByPlayerEnterReanimationEvent;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Medecin;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.RegionUtils;
import fr.karmaowner.utils.TimerUtils;
import fr.karmaowner.wantedlist.WantedList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MedecinEvents implements Listener {

  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    final PlayerData data = PlayerData.getPlayerData(p.getName());

    if (inventory.getName().equals(Jobs.NAMEACTIONINVENTORY) && 
      item != null && 
      item.getType() != Material.AIR && 
      data.selectedJob instanceof Medecin) {
      final Medecin m = (Medecin)data.selectedJob;
      if (item.getItemMeta().getDisplayName().equals(Medecin.Action.DEPOSERCADAVREMORGUE.getDisplayName())) {
        PlayerUtils utils = new PlayerUtils();
        utils.setInventory((Inventory)p.getInventory());
        utils.setItem(ItemUtils.getItem(Medecin.SACMORTUAIRE, (byte)0, 1, "Sac Mortuaire", null));
        if (utils.isOnPlayerInventoryByItemId(1)) {
          Entity target = data.selectedJob.getEntityTarget();
          if (target != null) {
              data.selectedJob.setEntityTarget(null);
              utils.removeItems(Medecin.SACMORTUAIRE, 1);
              target.remove();
              double money = 50.0D;
              //m.grade.addPointSuppl(500);
              data.setMoney(data.getMoney().add(BigDecimal.valueOf(money)));
              MessageUtils.sendMessage((CommandSender)p, "§aVous venez de déposer le cadavre");
              MessageUtils.sendMessage((CommandSender)p, "§aRécompense pour le bon geste: §2" + money + "€");
              p.closeInventory();

          } else {
            MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas de cadavre");
          } 
        } else {
          MessageUtils.sendMessage((CommandSender)p, "Vous n'avez pas de sac mortuaire pour effectuer cette action");
        } 
        return;
      } 

      p.closeInventory();
      e.setCancelled(true);
    } 
  }
  

}
