package fr.karmaowner.events.jobs;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.RebelleTerroriste;
import fr.karmaowner.utils.InventoryUtils;
import fr.karmaowner.utils.PlayerUtils;

import java.nio.Buffer;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RebelleTerroristeEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (inventory.getName().equals(Jobs.NAMEACTIONINVENTORY)) {
      if (item != null &&
        item.getType() != Material.AIR && 
        data.selectedJob instanceof RebelleTerroriste) {

        final RebelleTerroriste rt = (RebelleTerroriste)data.selectedJob;
        if (rt.getTarget() != null) {

          final PlayerData dataOtage = PlayerData.getPlayerData(rt.getTarget().getName());
          RebelleTerroriste.Action a1 = RebelleTerroriste.Action.OTAGE;
          RebelleTerroriste.Action a2 = RebelleTerroriste.Action.FOUILLER;
          if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(a1.getDisplayName())) {

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.getTime() - rt.getTimeAction().getTime() >= 15000L) {

              if (RebelleTerroriste.getOtage(p) == null) {

                final PlayerUtils player = new PlayerUtils();
                player.setPlayer(p);
                String[] jobs = { Jobs.Job.REBELLE.getName(), Jobs.Job.TERRORISTE.getName() };
                for(int i = 0; i < jobs.length; i++)
                {
                  String job = jobs[i];
                  if (data.selectedJob.getFeatures().getName().equalsIgnoreCase(job)) {

                    ArrayList<Player> that = player.getNearbyPlayersByJob(10.0D, job, p);
                    ArrayList<Player> policiers = player.getNearbyPlayersByJob(35.0D, "Gendarme", p);
                    if (Jobs.Job.GENDARME.onlinePlayers().size() > 0) {

                      if (that.size() > 1) {

                        if (that.size() > policiers.size()) {

                          if (rt.getPrixNegociation() > 0) {
                            RebelleTerroriste.setOtage(p, rt.getTarget());
                            rt.setTimeAction();
                            rt.setTask(new TaskCreator(new CustomRunnable() {
                              private Timestamp start = new Timestamp(System.currentTimeMillis());

                              public void customRun() {
                                if (!player.isClose((Entity)rt.getTarget(), 3)) {
                                  rt.getTarget().teleport((Entity)p);
                                  rt.getTarget().sendMessage(ChatColor.RED + "Ne vous éloignez pas du preneur d'otage !");
                                }
                                if (dataOtage.isDeath) {
                                  p.sendMessage(ChatColor.RED + "L'otage est mort ! Il vient d'être libéré !");
                                  p.sendMessage(ChatColor.DARK_RED + "Fin de l'action ! Prenez la fuite !");
                                  RebelleTerroriste.removeOtage(p);
                                  rt.setTask(null);
                                  rt.setTarget(null);
                                  cancel();
                                }
                                Timestamp now = new Timestamp(System.currentTimeMillis());
                                if (now.getTime() - this.start.getTime() >= 30000L && player
                                        .getNearbyPlayersByJob(35.0D, "Gendarme", p).size() <= 0) {
                                  p.sendMessage(ChatColor.DARK_RED + "Prise d'otage terminée: Aucun gendarme dans les parages !");
                                  RebelleTerroriste.removeOtage(p);
                                  rt.setTask(null);
                                  rt.setTarget(null);
                                  rt.resetPrixNegociation();
                                  cancel();
                                }
                              }
                            },false, 0L, 20L));
                          } else {
                            p.sendMessage(ChatColor.YELLOW + "Pour accélérer les négociations avec les gendarmes, vous devez exécuter la commande " + ChatColor.GOLD + "/jobs Rebelle otage prix <Prix>" + ChatColor.YELLOW + " (min=100€;max=25000€) avant de prendre en otage !");
                          }
                        } else {
                          p.sendMessage(ChatColor.RED + "Il doit y avoir plus de " + job + "s que de gendarmes pour effectuer cette action !");
                        }
                      } else {
                        p.sendMessage(ChatColor.RED + "Vous devez être au moins 2 " + job + "s pour effectuer cette action !");
                      }
                    } else {
                      p.sendMessage(ChatColor.RED + "Action impossible: Aucun gendarme de connecté!");
                    }
                  }
                }
                return;
              } else {
                p.sendMessage(ChatColor.RED + "Vous avez déjà un otage !");
              } 
            } else {
              p.sendMessage(ChatColor.RED + "Vous devez attendre " + '\017' + " minutes avant de pouvoir effectuer à nouveau cette action !");
            } 
          } else if (item.getItemMeta().getDisplayName().equals(a2.getDisplayName())) {
            RebelleTerroriste.Action fouiller = RebelleTerroriste.Action.FOUILLER;
            if (item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(fouiller.getDisplayName())) {
              Inventory copy = Main.INSTANCE.getServer().createInventory(null, 36, ChatColor.DARK_AQUA + "Fouille");
              final ItemStack[] copyinv = InventoryUtils.copiedInventoryContents((Inventory)rt.getTarget().getInventory());
              p.openInventory(copy);
              dataOtage.isFouille = true;
              if (p.getOpenInventory().getTopInventory().getName().equals(ChatColor.DARK_AQUA + "Fouille"))
                p.getOpenInventory().getTopInventory().setContents(copyinv); 
              new TaskCreator(new CustomRunnable() {
                    public void customRun() {
                      if (!p.isOnline() || p.getOpenInventory() == null || !p.getOpenInventory().getTopInventory().getName().equals(ChatColor.DARK_AQUA + "Fouille") || rt
                              .getTarget() == null || !rt.getTarget().isOnline()) {
                        p.closeInventory();
                        cancel();
                        return;
                      } 
                      p.getOpenInventory().getTopInventory().setContents(copyinv);
                    }
                  },  false, 20L, 10L);
            } 
          } 
        } else {
          p.sendMessage(ChatColor.RED + "Vous n'avez plus de cible. Il se pourrait qu'elle se soit déconnecté ou qu'elle s'est éloigné de vous");
          p.closeInventory();
        } 
      } 
      e.setCancelled(true);
    } else if (inventory.getName().equals(ChatColor.DARK_AQUA + "Fouille") && data.selectedJob instanceof RebelleTerroriste) {
      e.setCancelled(true);
    } 
  }
}
