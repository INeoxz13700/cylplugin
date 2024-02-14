package fr.karmaowner.events.jobs;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Psychopathe;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.TimerUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PsychopatheEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (item != null && 
      item.getType() != Material.AIR && 
      inventory.getName().equals(Jobs.NAMEACTIONINVENTORY) && 
      data.selectedJob instanceof Psychopathe) {
      Psychopathe psy = (Psychopathe)data.selectedJob;
      if (System.currentTimeMillis() - psy.getTortureTimer() >= 300000L) {
        if (item.getItemMeta().getDisplayName().equals(Psychopathe.Action.TORTURERTETE.getDisplayName())) {
          boolean torture = psy.torturer(psy.getTarget(), Psychopathe.Action.TORTURERTETE);
          if (torture) {
            MessageUtils.sendMessage((CommandSender)p, "§aDébut de la torture");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Torture déjà en cours...");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(Psychopathe.Action.TORTURERCOEUR.getDisplayName())) {
          boolean torture = psy.torturer(psy.getTarget(), Psychopathe.Action.TORTURERCOEUR);
          if (torture) {
            MessageUtils.sendMessage((CommandSender)p, "§aDébut de la torture");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Torture déjà en cours...");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(Psychopathe.Action.TORTURERCRANE.getDisplayName())) {
          boolean torture = psy.torturer(psy.getTarget(), Psychopathe.Action.TORTURERCRANE);
          if (torture) {
            MessageUtils.sendMessage((CommandSender)p, "§aDébut de la torture");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Torture déjà en cours...");
          } 
          p.closeInventory();
        } else if (item.getItemMeta().getDisplayName().equals(Psychopathe.Action.TORTUREROREILLE.getDisplayName())) {
          boolean torture = psy.torturer(psy.getTarget(), Psychopathe.Action.TORTUREROREILLE);
          if (torture) {
            MessageUtils.sendMessage((CommandSender)p, "§aDébut de la torture");
          } else {
            MessageUtils.sendMessage((CommandSender)p, "Torture déjà en cours...");
          } 
          p.closeInventory();
        } 
      } else {
        int secondsElapsed = (int)(300L - (System.currentTimeMillis() - psy.getTortureTimer()) / 1000L);
        MessageUtils.sendMessage((CommandSender)p, "Vous devez attendre " + TimerUtils.formatString(secondsElapsed) + " avant de pouvoir torturer à nouveau");
        p.closeInventory();
      } 
      e.setCancelled(true);
    } 
  }
}
