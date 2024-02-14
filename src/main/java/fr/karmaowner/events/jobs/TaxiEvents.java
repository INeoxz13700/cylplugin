package fr.karmaowner.events.jobs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.gps.GPS;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Taxi;
import fr.karmaowner.jobs.chauffeur.Regions;
import fr.karmaowner.jobs.chauffeur.Traject;
import fr.karmaowner.utils.MessageUtils;
import java.math.BigDecimal;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TaxiEvents implements Listener {
  @EventHandler
  public void onClickInventory(InventoryClickEvent e) {
    ClickType type = e.getClick();
    Inventory inventory = e.getInventory();
    final Player p = (Player)e.getWhoClicked();
    ItemStack item = e.getCurrentItem();
    PlayerData data = PlayerData.getPlayerData(p.getName());
    if (item != null && 
      item.getType() != Material.AIR)
      if (inventory.getName().equals(Jobs.NAMEACTIONINVENTORY)) {
        if (data.selectedJob instanceof Taxi) {
          Taxi t = (Taxi)data.selectedJob;
          Taxi.Action a = Taxi.Action.ENMENER;
          if (item.getItemMeta().getDisplayName().equals(a.getDisplayName())) {
            if (Taxi.getRequests().getAcceptedRequestReceiver(p) == null) {
              Taxi.getRequests().sendRequest(p, t.getTarget());
              new TaskCreator(new CustomRunnable() {
                    public void customRun() {
                      if (!Taxi.getRequests().isRequestAccepted(p) && Taxi.getRequests().getRequestReceiver(p) != null)
                        Taxi.getRequests().removeRequest(Taxi.getRequests().getRequestReceiver(p)); 
                    }
                  },  false, 200L);
              p.closeInventory();
            } else {
              p.sendMessage(ChatColor.RED + "Vous avez déjà un client !");
            }
          }
        } 
        e.setCancelled(true);
      } else if (inventory.getName().equals(ChatColor.RED + "Destination")) {
        ProtectedRegion region = Regions.getRegionByItem(item);
        if (region != null) {
          Player sender = Taxi.getRequests().getAcceptedRequestSender(p);
          Player receiver = p;
          PlayerData dataSender = PlayerData.getPlayerData(sender.getName());
          PlayerData dataReceiver = PlayerData.getPlayerData(p.getName());
          Taxi taxi = (Taxi)dataSender.selectedJob;
          Location b = new Location(p.getWorld(), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
          taxi.setTraject(new Traject(p.getLocation(), b, region.getId()));
          double price = taxi.getTraject().getPrice();
          if (dataReceiver.getMoney().compareTo(BigDecimal.valueOf(price)) == 1 || dataReceiver
            .getMoney().compareTo(BigDecimal.valueOf(price)) == 0) {
            p.sendMessage(ChatColor.GREEN + "Vous avez choisi d'aller à " + item.getItemMeta().getDisplayName());
            sender.sendMessage(ChatColor.GREEN + "Votre client a choisi de se rendre à " + item.getItemMeta().getDisplayName());

            taxi.setTask(new TaskCreator(new CustomRunnable() {
              public void customRun() {

                //if(sender.getVehicle() != null) Bukkit.broadcastMessage(sender.getVehicle().getName());

                if(sender.getVehicle() != null && sender.getVehicle().getName().contains("DynamXVehicle"))
                {

                  if (dataSender.selectedJob instanceof Taxi) {

                    if(!sender.isOnline())
                    {
                      MessageUtils.sendMessage((CommandSender)taxi.getTarget(), "§cLe chauffeur s'est déconnecté .");
                      taxi.getTarget().closeInventory();
                      Taxi.getRequests().removeAcceptedRequest(taxi.getTarget());
                      taxi.setTraject(null);
                      dataReceiver.selectedJob.getTask().cancelTask();
                      dataReceiver.selectedJob.setTask(null);
                      taxi.getTask().cancelTask();
                      taxi.setTask(null);
                    }
                    else if(sender.isDead())
                    {
                      MessageUtils.sendMessage((CommandSender)taxi.getTarget(), "§cLe chauffeur est mort .");
                      taxi.getTarget().closeInventory();
                      Taxi.getRequests().removeAcceptedRequest(taxi.getTarget());
                      taxi.setTraject(null);
                      dataReceiver.selectedJob.getTask().cancelTask();
                      dataReceiver.selectedJob.setTask(null);
                      taxi.getTask().cancelTask();
                      taxi.setTask(null);
                    }
                    else if(receiver.isDead())
                    {
                      double distance = 0.0D;
                      if(sender.getVehicle() != null)
                      {
                        distance = taxi.getTraject().getDeparture().distance(sender.getVehicle().getLocation());
                      }
                      else
                      {
                        distance = taxi.getTraject().getDeparture().distance(sender.getLocation());
                      }
                      double price2 = 0.2D * distance;
                      dataReceiver.setMoney(dataReceiver.getMoney().subtract(BigDecimal.valueOf(price2)));
                      dataSender.setMoney(dataSender.getMoney().add(BigDecimal.valueOf(price2)));
                      MessageUtils.sendMessage((CommandSender)sender, "§aLe client est mort vous recevez : §d" + (float)price + "€");
                      Taxi.getRequests().removeAcceptedRequest(p);
                      taxi.setTraject(null);
                      dataReceiver.selectedJob.getTask().cancelTask();
                      dataReceiver.selectedJob.setTask(null);
                      taxi.getTask().cancelTask();
                      taxi.setTask(null);
                    }
                    else if(!receiver.isOnline())
                    {
                      double distance = 0.0D;
                      if(sender.getVehicle() != null)
                      {
                        distance = taxi.getTraject().getDeparture().distance(sender.getVehicle().getLocation());
                      }
                      else
                      {
                        distance = taxi.getTraject().getDeparture().distance(sender.getLocation());
                      }
                      double price2 = 0.2D * distance;
                      dataReceiver.setMoney(dataReceiver.getMoney().subtract(BigDecimal.valueOf(price2)));
                      dataSender.setMoney(dataSender.getMoney().add(BigDecimal.valueOf(price2)));
                      MessageUtils.sendMessage((CommandSender)sender, "§aLe client s'est deconnecté vous recevez : §d" + (float)price + "€");
                      Taxi.getRequests().removeAcceptedRequest(p);
                      taxi.setTraject(null);
                      dataReceiver.selectedJob.getTask().cancelTask();
                      dataReceiver.selectedJob.setTask(null);
                      taxi.getTask().cancelTask();
                      taxi.setTask(null);
                    }
                    else
                    {

                      if (taxi.getTraject() != null) {

                        Location l;
                        if(sender.getVehicle() != null)
                        {
                          l = sender.getVehicle().getLocation();
                        }
                        else
                        {
                          l = sender.getLocation();
                        }
                        double distanceSender = l.distance(taxi.getTraject().getArrived());


                        int x = (int)l.getX();
                        int y = (int)l.getY();
                        int z = (int)l.getZ();
                        String msg = ChatColor.RED + "Vous=" + x + "," + y + "," + z + ";Destination=" + taxi.getTraject().getCoord() + ChatColor.DARK_RED + "; Distance=" + (int)distanceSender;

                        sender.spigot().sendMessage(ChatMessageType.ACTION_BAR,  TextComponent.fromLegacyText(msg));
                        receiver.spigot().sendMessage(ChatMessageType.ACTION_BAR,  TextComponent.fromLegacyText(msg));


                        for (ProtectedRegion r : Main.WG.getRegionManager(sender.getWorld()).getApplicableRegions(l)) {
                          if (r.getId().equals(taxi.getTraject().getRegionName())) {
                            sender.sendMessage(ChatColor.DARK_AQUA + "Bon travail ! Vous êtes arrivé à destination.");
                            receiver.sendMessage(ChatColor.DARK_AQUA + "Vous voilà enfin arrivé à destination.");
                            sender.sendMessage(ChatColor.BLUE + "Vous venez de remporter " + ChatColor.DARK_PURPLE + taxi.getTraject().getPrice() + "€");
                            receiver.sendMessage(ChatColor.BLUE + "Votre compte en banque vient d'être débité de " + ChatColor.DARK_PURPLE + (float)taxi.getTraject().getPrice() + "€");
                            dataSender.setMoney(dataSender.getMoney().add(BigDecimal.valueOf(taxi.getTraject().getPrice())));
                            dataReceiver.setMoney(dataReceiver.getMoney().subtract(BigDecimal.valueOf(taxi.getTraject().getPrice())));
                            Taxi.getRequests().removeAcceptedRequest(receiver);
                            taxi.setTraject(null);
                            dataReceiver.selectedJob.getTask().cancelTask();
                            dataReceiver.selectedJob.setTask(null);
                            return;
                          }
                        }
                      }
                    }
                  }

                }
                else
                {
                  sender.sendMessage("§cVous devez monter dans votre véhicule.");
                }

              }
            }, false, 0L, 20L));

          } else {
            p.sendMessage(ChatColor.RED + "Vous n'avez pas assez de sous pour effectuer cette action");
            sender.sendMessage(ChatColor.RED + "Votre client a annulé la requête en raison de problème financier !");
            Taxi.getRequests().removeAcceptedRequest(p);
            taxi.setTraject(null);
            dataReceiver.selectedJob.getTask().cancelTask();
            dataReceiver.selectedJob.setTask(null);
          } 
          p.closeInventory();
        } 
        e.setCancelled(true);
      }  
  }

}
