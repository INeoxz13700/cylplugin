package fr.karmaowner.jobs.chauffeur;

import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.jobs.Taxi;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Request {
  public HashMap<Player, Player> sendedrequest = new HashMap<>();
  
  public HashMap<Player, Player> acceptedrequest = new HashMap<>();
  
  public Player getRequestSender(Player receiver) {
    for (Map.Entry<Player, Player> p1 : this.sendedrequest.entrySet()) {
      if (p1.getValue() == receiver || p1.getKey() == receiver)
        return p1.getKey(); 
    } 
    return null;
  }
  
  public Player getRequestReceiver(Player sender) {
    for (Map.Entry<Player, Player> p1 : this.sendedrequest.entrySet()) {
      if (p1.getKey() == sender || p1.getValue() == sender)
        return p1.getValue(); 
    } 
    return null;
  }
  
  public void sendRequest(Player sender, Player receiver) {
    Player s = getRequestSender(receiver);
    if (s == null) {
      this.sendedrequest.put(sender, receiver);
      sender.sendMessage(ChatColor.GREEN + "Votre requête est parvenu au destinataire !");
      receiver.sendMessage(ChatColor.GOLD + "Une requête de transport viens de vous être envoyé par un taxi. Vous avez " + ChatColor.YELLOW + "10 secondes " + ChatColor.GOLD + " pour y répondre en exécutant la commande " + ChatColor.YELLOW + "/jobs taxi accept " + ChatColor.GOLD + "ou " + ChatColor.YELLOW + "/jobs taxi ignore");
    } else {
      sender.sendMessage(ChatColor.RED + "Une requête est déjà en cours de traitement !");
    } 
  }
  
  public boolean isRequestAccepted(Player sender) {
    for (Player p : this.acceptedrequest.keySet()) {
      if (p == sender)
        return true; 
    } 
    return false;
  }
  
  public Player getAcceptedRequestSender(Player receiver) {
    for (Map.Entry<Player, Player> p1 : this.acceptedrequest.entrySet()) {
      if (p1.getValue() == receiver || p1.getKey() == receiver)
        return p1.getKey(); 
    } 
    return null;
  }
  
  public Player getAcceptedRequestReceiver(Player sender) {
    for (Map.Entry<Player, Player> p1 : this.acceptedrequest.entrySet()) {
      if (p1.getKey() == sender || p1.getValue() == sender)
        return p1.getValue(); 
    } 
    return null;
  }
  
  public void stopAcceptedRequest(Player receiver) {
    PlayerData dataReceiver = PlayerData.getPlayerData(receiver.getName());
    Player sender = getAcceptedRequestSender(receiver);
    if (sender != null) {
      PlayerData dataSender = PlayerData.getPlayerData(sender.getName());
      Taxi t = (Taxi)dataSender.selectedJob;
      if (t.getTraject() != null) {
        dataReceiver.selectedJob.getTask().cancelTask();
        dataReceiver.selectedJob.setTask(null);
        double price = t.getTraject().getPrice() - 0.2D * t.getTraject().getArrived().distance(sender.getLocation());
        price = (int)(price * 100.0D) / 100.0D;
        sender.sendMessage(ChatColor.RED + "Votre client a annulé la requête ! Vous venez de remporter " + ChatColor.DARK_RED + price + "€");
        dataSender.setMoney(dataSender.getMoney().add(BigDecimal.valueOf(price)));
        receiver.sendMessage(ChatColor.BLUE + "Votre compte en banque vient d'être débité de " + ChatColor.DARK_PURPLE + price + "€");
        dataReceiver.setMoney(dataReceiver.getMoney().subtract(BigDecimal.valueOf(price)));
        Taxi.getRequests().removeAcceptedRequest(receiver);
        t.setTraject(null);
      } else {
        receiver.sendMessage(ChatColor.RED + "Impossible d'executer cette commande à ce moment là!");
      } 
    } 
  }
  
  public void acceptRequest(final Player receiver) {
    final Player sender = getRequestSender(receiver);
    if (sender != null) {
      final PlayerData dataReceiver = PlayerData.getPlayerData(receiver.getName());
      final PlayerData dataSender = PlayerData.getPlayerData(sender.getName());
      receiver.sendMessage(ChatColor.GREEN + "Votre demande a été accepté. Vous avez 30 secondes pour choisir une destination !");
      sender.sendMessage(ChatColor.GREEN + "Requête acceptée: En attente de la réponse du client pour la destination...");
      this.acceptedrequest.put(sender, receiver);
      dataReceiver.selectedJob.setTask(new TaskCreator(new CustomRunnable() {
              public void customRun() {
                if (dataSender.isDeath) {
                  cancel();
                  sender.sendMessage(ChatColor.DARK_RED + "Requête annulée. Vous êtes mort ! Vous venez de perdre votre client.");
                  receiver.sendMessage(ChatColor.DARK_RED + "Requête annulée: Le chauffeur est mort.");
                } else if (dataReceiver.isDeath) {
                  cancel();
                  sender.sendMessage(ChatColor.DARK_RED + "Requête annulée. Votre client est mort.");
                  receiver.sendMessage(ChatColor.DARK_RED + "Requête annulée: Vous êtes mort.");
                } 
                if (receiver.getLocation().distance(sender.getLocation()) > 4.0D) {
                  receiver.teleport(sender.getLocation());
                  receiver.sendMessage(ChatColor.RED + "Ne vous éloignez-pas du taxi ! ");
                } 
              }
            },  false, 0L, 20L));
      new TaskCreator(new CustomRunnable() {
            private int time = 30;
            
            public void customRun() {
              if (((Taxi)dataSender.selectedJob).getTraject() != null) {
                cancel();
              } else {
                if (!receiver.getOpenInventory().getTitle().equals(Regions.getInventoryDestination().getTitle()))
                  receiver.openInventory(Regions.getInventoryDestination()); 
                if (Taxi.getRequests().getAcceptedRequestReceiver(sender) == null) {
                  cancel();
                  receiver.closeInventory();
                } 
              } 
              if (((Taxi)dataSender.selectedJob).getTraject() == null && this.time <= 0) {
                Request.this.acceptedrequest.remove(sender);
                receiver.sendMessage(ChatColor.RED + "Vous n'avez pas choisi de destination à temps. La demande a été automatiquement annulé !");
                sender.sendMessage(ChatColor.RED + "Votre client a annulé la requête !");
                receiver.closeInventory();
                if (dataReceiver.selectedJob.getTask() != null) {
                  dataReceiver.selectedJob.getTask().cancelTask();
                  dataReceiver.selectedJob.setTask(null);
                } 
              } 
              receiver.sendMessage(ChatColor.GREEN + "Il vous reste " + this.time + " secondes pour choisir la destination !");
              this.time--;
            }
          },  false, 0L, 20L);
      this.sendedrequest.remove(sender);
      return;
    } 
    receiver.sendMessage(ChatColor.RED + "Aucune requête trouvée !");
  }
  
  public void removeAcceptedRequest(Player receiver) {
    Player sender = getAcceptedRequestSender(receiver);
    if (sender != null)
      this.acceptedrequest.remove(sender); 
  }
  
  public void removeRequest(Player receiver) {
    Player sender = getRequestSender(receiver);
    if (sender != null) {
      this.sendedrequest.remove(sender);
      receiver.sendMessage(ChatColor.BLUE + "Requête ignorée !");
      sender.sendMessage(ChatColor.RED + "Le joueur a refusé votre demande de transport !");
      return;
    } 
    receiver.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas de requête !");
  }
}
